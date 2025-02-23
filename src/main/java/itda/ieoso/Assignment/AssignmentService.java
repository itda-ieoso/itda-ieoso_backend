package itda.ieoso.Assignment;

import itda.ieoso.ContentOrder.ContentOrderService;
import itda.ieoso.Course.Course;
import itda.ieoso.Course.CourseRepository;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.CourseAttendees.CourseAttendeesStatus;
import itda.ieoso.Exception.CustomException;
import itda.ieoso.Exception.ErrorCode;
import itda.ieoso.Lecture.Lecture;
import itda.ieoso.Lecture.LectureRepository;
import itda.ieoso.Submission.Submission;
import itda.ieoso.Submission.SubmissionRepository;
import itda.ieoso.Submission.SubmissionStatus;
import itda.ieoso.User.User;
import itda.ieoso.User.UserRepository;
import itda.ieoso.Video.Video;
import itda.ieoso.Video.VideoDto;
import itda.ieoso.VideoHistory.VideoHistory;
import itda.ieoso.VideoHistory.VideoHistoryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final CourseAttendeesRepository courseAttendeesRepository;
    private final LectureRepository lectureRepository;
    private final SubmissionRepository submissionRepository;
    private final ContentOrderService contentOrderService;
    private final UserRepository userRepository;

    // SecurityContext에서 현재 사용자 조회
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // 현재 로그인한 사용자의 이메일 가져오기
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    // assignment 생성
    @Transactional
    public AssignmentDTO.Response createAssignment(Long courseId, Long lectureId, Long userId) {
        User authenticatedUser = getAuthenticatedUser();
        // course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // lecture 조회
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(()-> new CustomException(ErrorCode.LECTURE_NOT_FOUND));

        // 권한 검증
        if (!course.getUser().getUserId().equals(authenticatedUser.getUserId())) {
            throw new CustomException(ErrorCode.COURSE_PERMISSION_DENIED);
        }

        // assignment 생성
        Assignment assignment = Assignment.builder()
                .course(course)
                .lecture(lecture)
                .assignmentTitle(null)
                .assignmentDescription(null)
                .startDate(LocalDateTime.of(course.getStartDate(), LocalTime.of(0, 0, 0)))
                .endDate(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .submissions(new ArrayList<>())
                .build();

        //  assignment 저장
        assignmentRepository.save(assignment);

        // contentOrder 생성
        contentOrderService.createContentOrder(course, lecture,"assignment", assignment.getAssignmentId());

        // submission 생성
        addSubmissionToAssignment(course,assignment);

        // 반환
        AssignmentDTO.Response response = AssignmentDTO.Response.of(assignment);

        return response;
    }

    // assignment 업데이트
    @Transactional
    public AssignmentDTO.Response updateAssignment(Long courseId, Long assignmentId, Long userId, AssignmentDTO.Request request) {
        User authenticatedUser = getAuthenticatedUser();
        // course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // 권한 검증
        if (!course.getUser().getUserId().equals(authenticatedUser.getUserId())) {
            throw new CustomException(ErrorCode.COURSE_PERMISSION_DENIED);
        }

        // assignment 조회
        Assignment assignment = assignmentRepository.findByCourseAndAssignmentId(course, assignmentId);
        if (assignment == null) {
            throw new CustomException(ErrorCode.ASSIGNMENT_NOT_FOUND);
        }

        if (request.endDate() != null) {
            if (request.endDate().toLocalDate().isBefore(course.getStartDate()) || request.endDate().toLocalDate().isAfter(course.getEndDate())) {
                throw new CustomException(ErrorCode.INVALID_DATE_RANGE);
            }
        }

        // assignment 수정
        if (request.assignmentTitle()!=null) assignment.setAssignmentTitle(request.assignmentTitle());
        if (request.assignmentDescription()!=null) assignment.setAssignmentDescription(request.assignmentDescription());
        // if (request.startDate()!=null) assignment.setStartDate(request.startDate());
        if (request.endDate()!=null) assignment.setEndDate(request.endDate());
        assignment.setUpdatedAt(LocalDateTime.now());

        assignmentRepository.save(assignment);

        // 반환
        AssignmentDTO.Response response = AssignmentDTO.Response.of(assignment);

        return response;

    }

    // assignment 삭제
    @Transactional
    public AssignmentDTO.deleteResponse deleteAssignment(Long courseId, Long assignmentId, Long userId) {
        User authenticatedUser = getAuthenticatedUser();
        // course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // 권한 검증
        if (!course.getUser().getUserId().equals(authenticatedUser.getUserId())) {
            throw new CustomException(ErrorCode.COURSE_PERMISSION_DENIED);
        }

        // assignment 조회
        Assignment assignment = assignmentRepository.findByCourseAndAssignmentId(course, assignmentId);
        if (assignment == null) {
            throw new CustomException(ErrorCode.ASSIGNMENT_NOT_FOUND);
        }

        // submission 삭제 (추후 수정후 삭제)
        submissionRepository.deleteAllByAssignment(assignment);

        // contentOrder 삭제
        contentOrderService.deleteContentOrder(assignmentId, "assignment");

        // assignment 삭제
        assignmentRepository.delete(assignment);

        // 반환
        AssignmentDTO.deleteResponse response = AssignmentDTO.deleteResponse.builder()
                .assignmentId(assignmentId)
                .message("assignment 삭제 완료")
                .build();
        return response;

    }


    // courseAttendees만큼의 submission 생성
    private void addSubmissionToAssignment(Course course, Assignment assignment) {
        // course내의 모든 courseAttendees 조회
        List<CourseAttendees> attendees = courseAttendeesRepository.findAllByCourse(course);

        // submission 생성
        List<Submission> submissionList = attendees.stream()
                .filter(attendee -> attendee.getCourseAttendeesStatus()== CourseAttendeesStatus.ACTIVE)
                .map(attendee -> Submission.builder()
                        .course(course)
                        .assignment(assignment)
                        .courseAttendees(attendee)
                        .user(attendee.getUser())
                        .submissionStatus(SubmissionStatus.NOT_SUBMITTED)
                        .build())
                .collect(Collectors.toList());

        // assignment에 submission 추가
        submissionRepository.saveAll(submissionList);

        // assignment.getSubmissions().addAll(submissionList);
        // assignmentRepository.save(assignment);
    }

    public AssignmentDTO.Response getAssignment(Long assignmentId) {
        // 과제 정보 조회
        Assignment assignment = assignmentRepository.findByAssignmentId(assignmentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        return AssignmentDTO.Response.of(assignment);
    }

}