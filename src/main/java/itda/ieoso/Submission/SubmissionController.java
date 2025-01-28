package itda.ieoso.Submission;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assignments/{assignmentId}/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    // 과제 제출 및 수정
    @PutMapping("/{submissionId}/{userId}")
    public ResponseEntity<SubmissionDTO> updateSubmission(
            @PathVariable Long assignmentId,
            @PathVariable Long submissionId,
            @PathVariable Long userId,
            @RequestBody Submission submissionRequest) {

        // 제출 정보 수정 처리
        SubmissionDTO updatedSubmissionDTO = submissionService.updateSubmission(assignmentId, submissionId, userId, submissionRequest.getTextContent(), submissionRequest.getFileUrl());
        return ResponseEntity.ok(updatedSubmissionDTO); // 수정된 제출 정보 반환
    }

    // 과제 삭제(과제 상태 초기화)
    @PutMapping("/delete/{submissionId}/{userId}")
    public ResponseEntity<Void> deleteSubmission(
            @PathVariable Long assignmentId,
            @PathVariable Long submissionId,
            @PathVariable Long userId) {

        // 제출 정보 수정 처리
        submissionService.deleteSubmission(assignmentId, submissionId, userId);

        return ResponseEntity.noContent().build(); // 삭제 완료 응답
    }

    // 과제 조회
    @GetMapping("/{submissionId}/{userId}")
    public ResponseEntity<SubmissionDTO> getSubmission(
            @PathVariable Long assignmentId,
            @PathVariable Long submissionId,
            @PathVariable Long userId) {

        // 제출 정보를 가져와서 SubmissionDTO로 변환
        SubmissionDTO submissionDTO = submissionService.getSubmission(assignmentId, submissionId, userId);
        return ResponseEntity.ok(submissionDTO); // 조회한 제출 정보 반환
    }
}



