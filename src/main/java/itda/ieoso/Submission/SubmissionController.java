package itda.ieoso.Submission;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses/{courseId}/lectures/{lectureId}/assignments/{assignmentId}/submissions")
public class SubmissionController {

//    private final SubmissionService submissionService;
//
//    public SubmissionController(SubmissionService submissionService) {
//        this.submissionService = submissionService;
//    }
//
//    // 과제 제출
//    @PostMapping
//    public ResponseEntity<Void> submitAssignment(
//            @PathVariable String courseId,
//            @PathVariable String lectureId,
//            @PathVariable String assignmentId,
//            @RequestParam(required = false) String textContent,
//            @RequestParam(required = false) String fileUrl,
//            @RequestParam String userId
//    ) {
//        submissionService.submitAssignment(courseId, lectureId, assignmentId, userId, textContent, fileUrl);
//        return ResponseEntity.ok().build();
//    }
//    // 과제 수정
//    @PutMapping("/{submissionId}")
//    public ResponseEntity<Submission> updateSubmission(
//            @PathVariable String courseId,
//            @PathVariable String lectureId,
//            @PathVariable String assignmentId,
//            @PathVariable String submissionId,
//            @RequestParam(required = false) String textContent,
//            @RequestParam(required = false) String fileUrl
//    ) {
//        Submission updatedSubmission = submissionService.updateSubmission(courseId, lectureId, assignmentId, submissionId, textContent, fileUrl);
//        return ResponseEntity.ok(updatedSubmission);
//    }
//
//    // 과제 삭제
//    @DeleteMapping("/{submissionId}")
//    public ResponseEntity<Void> deleteSubmission(
//            @PathVariable String courseId,
//            @PathVariable String lectureId,
//            @PathVariable String assignmentId,
//            @PathVariable String submissionId
//    ) {
//        submissionService.deleteSubmission(courseId, lectureId, assignmentId, submissionId);
//        return ResponseEntity.noContent().build();
//    }
//
//    // 과제 조회
//    @GetMapping("/{submissionId}")
//    public ResponseEntity<Submission> getSubmission(
//            @PathVariable String courseId,
//            @PathVariable String lectureId,
//            @PathVariable String assignmentId,
//            @PathVariable String submissionId
//    ) {
//        Submission submission = submissionService.getSubmission(courseId, lectureId, assignmentId, submissionId);
//        return ResponseEntity.ok(submission);
//    }
}
