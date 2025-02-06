package itda.ieoso.Assignment;

import itda.ieoso.Course.Course;
import itda.ieoso.Course.CourseRepository;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.CourseAttendees.CourseAttendeesStatus;
import itda.ieoso.Lecture.Lecture;
import itda.ieoso.Lecture.LectureRepository;
import itda.ieoso.Submission.Submission;
import itda.ieoso.Submission.SubmissionRepository;
import itda.ieoso.Submission.SubmissionStatus;
import itda.ieoso.Video.Video;
import itda.ieoso.Video.VideoDto;
import itda.ieoso.VideoHistory.VideoHistory;
import itda.ieoso.VideoHistory.VideoHistoryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    // assignment 생성
    @Transactional
    public AssignmentDTO.Response createVideo(Long courseId, Long lectureId, Long userId, AssignmentDTO.createRequest request) {
        // course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new IllegalArgumentException("강좌를 찾을수없습니다."));

        // lecture 조회
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(()-> new IllegalArgumentException("챕터를 찾을수없습니다."));

        // 권한 검증
        if (!course.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("강의 개설자가 아닙니다.");
        }

        // assignment 생성
        Assignment assignment = Assignment.builder()
                .course(course)
                .lecture(lecture)
                .assignmentTitle(request.assignmentTitle())
                .assignmentDescription(request.assignmentDescription())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .submissions(new ArrayList<>())
                .build();

        //  assignment 저장
        assignmentRepository.save(assignment);

        // submission 생성
        addSubmissionToAssignment(course,assignment);

        // 반환
        AssignmentDTO.Response response = AssignmentDTO.Response.builder()
                .assignmentId(assignment.getAssignmentId())
                .assignmentTitle(assignment.getAssignmentTitle())
                .assignmentDescription(assignment.getAssignmentDescription())
                .startDate(assignment.getStartDate())
                .endDate(assignment.getEndDate())
                .build();

        return response;
    }

    // assignment 업데이트
    @Transactional
    public AssignmentDTO.Response updateVideo(Long courseId, Long assignmentId, Long userId, AssignmentDTO.updateRequest request) {
        // course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new IllegalArgumentException("강좌를 찾을수없습니다."));

        // 권한 검증
        if (!course.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("강의 개설자가 아닙니다.");
        }

        // assignment 조회
        Assignment assignment = assignmentRepository.findByCourseAndAssignmentId(course, assignmentId);
        if (assignment == null) {
            throw new IllegalArgumentException("assignment를 찾을수없습니다.");
        }

        // assignment 수정
        if (request.assignmentTitle()!=null) assignment.setAssignmentTitle(request.assignmentTitle());
        if (request.assignmentDescription()!=null) assignment.setAssignmentDescription(request.assignmentDescription());
        if (request.startDate()!=null) assignment.setStartDate(request.startDate());
        if (request.endDate()!=null) assignment.setEndDate(request.endDate());
        assignment.setUpdatedAt(LocalDateTime.now());

        assignmentRepository.save(assignment);

        // 반환
        AssignmentDTO.Response response = AssignmentDTO.Response.builder()
                .assignmentId(assignment.getAssignmentId())
                .assignmentTitle(assignment.getAssignmentTitle())
                .assignmentDescription(assignment.getAssignmentDescription())
                .startDate(assignment.getStartDate())
                .endDate(assignment.getEndDate())
                .build();

        return response;

    }

    // assignment 삭제
    @Transactional
    public AssignmentDTO.deleteResponse deleteVideo(Long courseId, Long assignmentId, Long userId) {
        // course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new IllegalArgumentException("강좌를 찾을수없습니다."));

        // 권한 검증
        if (!course.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("강의 개설자가 아닙니다.");
        }

        // assignment 조회
        Assignment assignment = assignmentRepository.findByCourseAndAssignmentId(course, assignmentId);
        if (assignment == null) {
            throw new IllegalArgumentException("assignment를 찾을수없습니다.");
        }

        // submission 삭제 (추후 수정후 삭제)
        submissionRepository.deleteAllByAssignment(assignment);

        // assignment 삭제
        assignmentRepository.delete(assignment);

        // 반환
        AssignmentDTO.deleteResponse response = AssignmentDTO.deleteResponse.builder()
                .assignmentId(assignmentId)
                .message("assignment 삭제 완료")
                .build();
        return response;

    }

    // assignment 조회
    public void getVideo() {

    }

    // assignment 목록 조회
    public void getVideos() {

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


}