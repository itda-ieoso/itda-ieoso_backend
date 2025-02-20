package itda.ieoso.Submission;

import itda.ieoso.File.S3Service;
import itda.ieoso.Response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/assignments/{assignmentId}/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    private final S3Service S3Service;

    public SubmissionController(SubmissionService submissionService, S3Service S3Service) {
        this.submissionService = submissionService;
        this.S3Service = S3Service;
    }

    // 과제 제출 및 수정
    @PutMapping("/{submissionId}/{userId}")
    public Response<SubmissionDTO> updateSubmission(
            @PathVariable Long assignmentId,
            @PathVariable Long submissionId,
            @PathVariable Long userId,
            @RequestParam(value = "textContent") String textContent,
            @RequestParam(required = false) List<String> existingFileUrls,  // 기존 파일 URL
            @RequestParam(required = false) List<String> deleteFileUrls,    // 삭제할 파일 URL
            @RequestParam(value = "files") MultipartFile[] newFiles) throws IOException {  // MultipartFile[]로 받기

        // 제출 정보 수정 처리
        SubmissionDTO updatedSubmissionDTO = submissionService.updateSubmission(assignmentId,
                submissionId,
                userId,
                textContent,
                existingFileUrls,   // 새로운 파라미터 추가
                deleteFileUrls,     // 새로운 파라미터 추가
                newFiles);

        return Response.success("과제 제출 및 수정", updatedSubmissionDTO); // 수정된 제출 정보 반환
    }

    // 과제 삭제(과제 상태 초기화)
    @PutMapping("/delete/{submissionId}/{userId}")
    public Response<Void> deleteSubmission(
            @PathVariable Long assignmentId,
            @PathVariable Long submissionId,
            @PathVariable Long userId) {

        // 제출 정보 수정 처리
        submissionService.deleteSubmission(assignmentId, submissionId, userId);

        return Response.success("과제 제출 삭제", null); // 삭제 완료 응답
    }

    // 과제 조회
    @GetMapping("/{submissionId}/{userId}")
    public Response<SubmissionDTO> getSubmission(
            @PathVariable Long assignmentId,
            @PathVariable Long submissionId,
            @PathVariable Long userId) {

        // 제출 정보를 가져와서 SubmissionDTO로 변환
        SubmissionDTO submissionDTO = submissionService.getSubmission(assignmentId, submissionId, userId);
        return Response.success("과제 조회", submissionDTO); // 조회한 제출 정보 반환
    }
}



