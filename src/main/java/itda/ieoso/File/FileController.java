package itda.ieoso.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/files")  // 파일 관련 API 경로
public class FileController {

    private final S3Service s3Service;

    @Autowired
    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    // 파일 업로드 API (프로필 사진이나 다른 파일 업로드용)
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 제공되지 않았습니다.");
        }

        try {
            // 파일이 업로드될 폴더 지정
            String folder = "user_files";  // 예: 사용자 관련 파일 폴더
            String filename = file.getOriginalFilename();  // 파일명 (혹은 고유한 이름 사용 가능)

            // MultipartFile을 File로 변환
            File tempFile = File.createTempFile("upload-", filename);
            file.transferTo(tempFile);

            // S3에 파일 업로드
            String fileUrl = s3Service.uploadFile(folder, filename, tempFile);

            // 임시 파일 삭제
            tempFile.delete();

            return ResponseEntity.ok("파일 업로드 성공: " + fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("파일 업로드 실패: " + e.getMessage());
        }
    }

    // 파일 다운로드 URL 생성 API (Presigned URL)
    @GetMapping("/download/{filename}")
    public ResponseEntity<String> getDownloadUrl(@PathVariable String filename) {
        try {
            // S3에 저장된 파일의 다운로드 URL을 Presigned URL로 생성
            String fileUrl = s3Service.generatePresignedUrl(filename);

            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("파일 다운로드 URL 생성 실패: " + e.getMessage());
        }
    }
}


