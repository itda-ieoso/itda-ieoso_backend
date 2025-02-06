package itda.ieoso.Submission;

import itda.ieoso.File.S3Service;
import itda.ieoso.Response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
            @RequestParam(value = "files") MultipartFile[] files) throws IOException {  // MultipartFile[]로 받기

        // 제출 정보 수정 처리
        SubmissionDTO updatedSubmissionDTO = submissionService.updateSubmission(assignmentId, submissionId, userId, textContent, files);

        return Response.success("과제 제출 및 수정", updatedSubmissionDTO); // 수정된 제출 정보 반환
    }

    // 파일 다운로드 밖으로 빼기
    @GetMapping("/download")
    public Response<String> getDownloadUrl(@RequestParam("fileUrl") String fileUrl) {
        try {
            // fileUrl에서 S3 도메인 부분 제거 후 key만 추출
            String fileKey = fileUrl.replace("https://your-s3-bucket.s3.amazonaws.com/", "");

            // Presigned URL 생성
            String presignedUrl = S3Service.generatePresignedUrl(fileKey);

            return Response.success("과제 제출파일 다운로드", presignedUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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



