package itda.ieoso.Statistic;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.Assignment.AssignmentRepository;
import itda.ieoso.Course.Course;
import itda.ieoso.Course.CourseRepository;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.CourseAttendees.CourseAttendeesStatus;
import itda.ieoso.Exception.CustomException;
import itda.ieoso.Exception.ErrorCode;
import itda.ieoso.Lecture.Lecture;
import itda.ieoso.Submission.Submission;
import itda.ieoso.Submission.SubmissionFile;
import itda.ieoso.Submission.SubmissionRepository;
import itda.ieoso.Submission.SubmissionStatus;
import itda.ieoso.User.User;
import itda.ieoso.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final CourseRepository courseRepository;
    private final CourseAttendeesRepository courseAttendeesRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    @Autowired
    public StatisticsService(CourseRepository courseRepository,
                             CourseAttendeesRepository courseAttendeesRepository,
                             AssignmentRepository assignmentRepository,
                             SubmissionRepository submissionRepository,
                             UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.courseAttendeesRepository = courseAttendeesRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
    }

    // SecurityContext에서 현재 사용자 조회
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // 현재 로그인한 사용자의 이메일 가져오기
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public List<AssignmentStatisticsDTO> getAssignmentStatistics(Long courseId) {
        // 1. Course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // 로그인 유저 조회
        User authenticatedUser = getAuthenticatedUser();

        // courseAttendees 조회
        CourseAttendees courseAttendees = courseAttendeesRepository.findByCourseAndUser(course, authenticatedUser)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSEATTENDEES_PERMISSION_DENIED));

        // 과제 전체공개 여부 확인
        validateCourseAssignmentVisibility(course, courseAttendees);

        // 2. Course에 연결된 Lecture ID만 가져오기
        List<Long> lectureIds = course.getLectures().stream()
                .map(Lecture::getLectureId) // Lecture 객체 대신 lectureId만 추출
                .collect(Collectors.toList());

        // 3. Lecture ID로 Assignment 조회
        List<Assignment> allAssignments = assignmentRepository.findByLecture_LectureIdIn(lectureIds);

        // 4. 통계 처리
        return allAssignments.stream().map(assignment -> {
            List<Submission> submissions = submissionRepository.findByAssignment_AssignmentId(assignment.getAssignmentId());

            // 학생별 제출 상태 계산
            List<CourseAttendees> attendees = courseAttendeesRepository.findByCourse_CourseId(courseId);
            List<AssignmentStatisticsDTO.StudentSubmissionStatus> studentStatuses = attendees.stream()
                    .filter(attendee -> attendee.getCourseAttendeesStatus() == CourseAttendeesStatus.ACTIVE)
                    .map(attendee -> {
                        User student = attendee.getUser();
                        Submission submission = submissions.stream()
                                .filter(sub -> sub.getUser().getUserId().equals(student.getUserId()))
                                .findFirst()
                                .orElse(null);

                        String status = (submission == null) ? "NOT_SUBMITTED" :
                                (submission.getSubmissionStatus() == SubmissionStatus.NOT_SUBMITTED) ? "NOT_SUBMITTED" :
                                        (submission.getSubmissionStatus() == SubmissionStatus.LATE) ? "LATE" :
                                                "SUBMITTED";

                        return new AssignmentStatisticsDTO.StudentSubmissionStatus(student.getUserId(), student.getName(), status);
                    })
                    .collect(Collectors.toList());

            return new AssignmentStatisticsDTO(assignment.getAssignmentId(), assignment.getAssignmentTitle(), studentStatuses);
        }).collect(Collectors.toList());
    }

    public List<AssignmentSubmissionDTO> getAllAssignmentSubmissions(Long courseId) {
        // 1. Course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // 로그인 유저 조회
        User authenticatedUser = getAuthenticatedUser();

        // courseAttendees 조회
        CourseAttendees courseAttendees = courseAttendeesRepository.findByCourseAndUser(course, authenticatedUser)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSEATTENDEES_PERMISSION_DENIED));

        // 과제 전체공개 여부 확인
        validateCourseAssignmentVisibility(course, courseAttendees);

        // 2. Course에 연결된 Lecture ID 가져오기
        List<Long> lectureIds = course.getLectures().stream()
                .map(Lecture::getLectureId)
                .collect(Collectors.toList());

        // 3. Lecture ID를 기준으로 Assignment 조회
        List<Assignment> allAssignments = assignmentRepository.findByLecture_LectureIdIn(lectureIds);

        // 4. 각 과제별 제출 결과 조회
        return allAssignments.stream().map(assignment -> {
            // 해당 과제에 대한 모든 제출 기록 조회
            List<Submission> submissions = submissionRepository.findByAssignment_AssignmentId(assignment.getAssignmentId());

            // 5. 학생별 제출 결과 계산
            List<CourseAttendees> attendees = courseAttendeesRepository.findByCourse_CourseId(courseId);
            List<AssignmentSubmissionDTO.StudentSubmissionResult> studentResults = attendees.stream()
                    .filter(attendee -> attendee.getCourseAttendeesStatus() == CourseAttendeesStatus.ACTIVE)
                    .map(attendee -> {
                        User student = attendee.getUser();
                        Submission submission = submissions.stream()
                                .filter(sub -> sub.getUser().getUserId().equals(student.getUserId()))
                                .findFirst()
                                .orElse(null);

                        String status = "NOT_SUBMITTED";
                        LocalDateTime submittedAt = null;
                        String textContent = null;
                        List<AssignmentSubmissionDTO.SubmissionFileDTO> fileList = new ArrayList<>();

                        // 제출 데이터가 있는 경우
                        if (submission != null) {
                            if (submission.getSubmissionStatus() == SubmissionStatus.NOT_SUBMITTED) {
                                status = "NOT_SUBMITTED";
                            } else if (submission.getSubmissionStatus() == SubmissionStatus.LATE) {
                                status = "LATE";
                            } else {
                                status = "SUBMITTED";
                            }
                            submittedAt = submission.getSubmittedAt();
                            textContent = submission.getTextContent();


                            // SubmissionFile 전체 리스트를 저장
                            if (submission.getSubmissionFiles() != null && !submission.getSubmissionFiles().isEmpty()) {
                                fileList = submission.getSubmissionFiles().stream()
                                        .map(file -> new AssignmentSubmissionDTO.SubmissionFileDTO(
                                                file.getSubmissionOriginalFilename(),
                                                file.getSubmissionFileUrl()
                                        ))
                                        .collect(Collectors.toList());
                            }
                        }

                        return new AssignmentSubmissionDTO.StudentSubmissionResult(
                                student.getUserId(),
                                student.getName(),
                                fileList,
                                submittedAt,
                                status,
                                textContent
                        );
                    })
                    .collect(Collectors.toList());

            return new AssignmentSubmissionDTO(
                    assignment.getAssignmentId(),
                    assignment.getAssignmentTitle(),
                    studentResults
            );
        }).collect(Collectors.toList());
    }

    void validateCourseAssignmentVisibility(Course course, CourseAttendees courseAttendees) {
        // courseAttendees 가 owner -> 공개
        if (courseAttendees.getCourseAttendeesStatus()==CourseAttendeesStatus.OWNER) {
            return;
        } else if (courseAttendees.getCourseAttendeesStatus()==CourseAttendeesStatus.ACTIVE && course.getIsAssignmentPublic()) {
            return;
        } else {
            throw new CustomException(ErrorCode.FORBIDDEN_ASSIGNMENT_ACCESS);
        }
    }

}

