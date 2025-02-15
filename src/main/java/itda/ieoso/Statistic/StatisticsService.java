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
import itda.ieoso.Submission.SubmissionRepository;
import itda.ieoso.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final CourseRepository courseRepository;
    private final CourseAttendeesRepository courseAttendeesRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;

    @Autowired
    public StatisticsService(CourseRepository courseRepository,
                             CourseAttendeesRepository courseAttendeesRepository,
                             AssignmentRepository assignmentRepository,
                             SubmissionRepository submissionRepository) {
        this.courseRepository = courseRepository;
        this.courseAttendeesRepository = courseAttendeesRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
    }

    public List<AssignmentStatisticsDTO> getAssignmentStatistics(Long courseId) {
        // 1. Course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

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
                                (submission.getSubmissionStatus().equals("NOT_SUBMITTED")) ? "NOT_SUBMITTED" :
                                        "SUBMITTED";

                        return new AssignmentStatisticsDTO.StudentSubmissionStatus(student.getUserId(), student.getName(), status);
                    })
                    .collect(Collectors.toList());

            return new AssignmentStatisticsDTO(assignment.getAssignmentId(), assignment.getAssignmentTitle(), studentStatuses);
        }).collect(Collectors.toList());
    }


}

