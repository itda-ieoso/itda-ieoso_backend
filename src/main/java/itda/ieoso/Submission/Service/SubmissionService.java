package itda.ieoso.Submission.Service;

import itda.ieoso.Assignment.Repository.AssignmentRepository;
import itda.ieoso.ClassStudent.Repository.ClassStudentRepository;
import itda.ieoso.Course.Repository.CourseRepository;
import itda.ieoso.Submission.Domain.Submission;
import itda.ieoso.Submission.Domain.SubmissionStatus;
import itda.ieoso.Submission.Repository.SubmissionRepository;
import itda.ieoso.User.Domain.User;
import itda.ieoso.User.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final ClassStudentRepository classStudentRepository;
    private final UserRepository userRepository;

    public SubmissionService(SubmissionRepository submissionRepository, AssignmentRepository assignmentRepository,
                             CourseRepository courseRepository, ClassStudentRepository classStudentRepository, UserRepository userRepository) {
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.classStudentRepository = classStudentRepository;
        this.userRepository = userRepository;
    }

    public void submitAssignment(String courseId, String lectureId, String assignmentId, String userId,
                                 String textContent, String fileUrl) {
        // 학습자 권한 확인
        if (!classStudentRepository.existsByCourse_CourseIdAndUser_UserId(courseId, userId)) {
            throw new IllegalArgumentException("Only enrolled learners can submit assignments.");
        }

        // 과제 확인
        var assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        Submission submission = new Submission();
        submission.setSubmissionId(UUID.randomUUID().toString());
        submission.setUser(user); // 유저 정보 가져오기
        submission.setAssignment(assignment);
        submission.setSubmittedAt(new Date());
        submission.setSubStatus(new Date().after(assignment.getDueDate()) ? SubmissionStatus.LATE : SubmissionStatus.SUBMITTED);

        // 텍스트 내용 설정
        submission.setTextContent(textContent);

        // 파일 URL 설정
        submission.setFileUrl(fileUrl);

        submissionRepository.save(submission);
    }
    // Update submission
    public Submission updateSubmission(String courseId, String lectureId, String assignmentId, String submissionId, String textContent, String fileUrl) {
        Optional<Submission> optionalSubmission = submissionRepository.findById(submissionId);
        if (optionalSubmission.isPresent()) {
            Submission submission = optionalSubmission.get();
            if (textContent != null) {
                submission.setTextContent(textContent);
            }
            if (fileUrl != null) {
                submission.setFileUrl(fileUrl);
            }
            return submissionRepository.save(submission);
        } else {
            throw new IllegalArgumentException("Submission not found");
        }
    }

    // Delete submission
    public void deleteSubmission(String courseId, String lectureId, String assignmentId, String submissionId) {
        submissionRepository.deleteById(submissionId);
    }

    // Get submission
    public Submission getSubmission(String courseId, String lectureId, String assignmentId, String submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
    }
}