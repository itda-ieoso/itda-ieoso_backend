package itda.ieoso.Submission;

import itda.ieoso.Assignment.AssignmentRepository;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.Course.CourseRepository;
import itda.ieoso.User.User;
import itda.ieoso.User.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubmissionService {

//    private final SubmissionRepository submissionRepository;
//    private final AssignmentRepository assignmentRepository;
//    private final CourseRepository courseRepository;
//    private final CourseAttendeesRepository courseAttendeesRepository;
//    private final UserRepository userRepository;
//
//    public SubmissionService(SubmissionRepository submissionRepository, AssignmentRepository assignmentRepository,
//                             CourseRepository courseRepository, CourseAttendeesRepository courseAttendeesRepository, UserRepository userRepository) {
//        this.submissionRepository = submissionRepository;
//        this.assignmentRepository = assignmentRepository;
//        this.courseRepository = courseRepository;
//        this.courseAttendeesRepository = courseAttendeesRepository;
//        this.userRepository = userRepository;
//    }
//
//    public void submitAssignment(String courseId, String lectureId, String assignmentId, String userId,
//                                 String textContent, String fileUrl) {
//        // 학습자 권한 확인
//        if (!courseAttendeesRepository.existsByCourse_CourseIdAndUser_UserId(courseId, userId)) {
//            throw new IllegalArgumentException("Only enrolled learners can submit assignments.");
//        }
//
//        // 과제 확인
//        var assignment = assignmentRepository.findById(assignmentId)
//                .orElseThrow(() -> new IllegalArgumentException("Assignment not found."));
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found."));
//
//        Submission submission = new Submission();
//        submission.setSubmissionId(UUID.randomUUID().toString());
//        submission.setUser(user); // 유저 정보 가져오기
//        submission.setAssignment(assignment);
//        submission.setSubmittedAt(new Date());
//        submission.setSubStatus(new Date().after(assignment.getDueDate()) ? SubmissionStatus.LATE : SubmissionStatus.SUBMITTED);
//
//        // 텍스트 내용 설정
//        submission.setTextContent(textContent);
//
//        // 파일 URL 설정
//        submission.setFileUrl(fileUrl);
//
//        submissionRepository.save(submission);
//    }
//    // Update submission
//    public Submission updateSubmission(String courseId, String lectureId, String assignmentId, String submissionId, String textContent, String fileUrl) {
//        Optional<Submission> optionalSubmission = submissionRepository.findById(submissionId);
//        if (optionalSubmission.isPresent()) {
//            Submission submission = optionalSubmission.get();
//            if (textContent != null) {
//                submission.setTextContent(textContent);
//            }
//            if (fileUrl != null) {
//                submission.setFileUrl(fileUrl);
//            }
//            return submissionRepository.save(submission);
//        } else {
//            throw new IllegalArgumentException("Submission not found");
//        }
//    }
//
//    // Delete submission
//    public void deleteSubmission(String courseId, String lectureId, String assignmentId, String submissionId) {
//        submissionRepository.deleteById(submissionId);
//    }
//
//    // Get submission
//    public Submission getSubmission(String courseId, String lectureId, String assignmentId, String submissionId) {
//        return submissionRepository.findById(submissionId)
//                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
//    }
}