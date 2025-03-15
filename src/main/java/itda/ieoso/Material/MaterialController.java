package itda.ieoso.Material;

import itda.ieoso.File.S3Service;
import itda.ieoso.MaterialHistory.MaterialHistory;
import itda.ieoso.MaterialHistory.MaterialHistoryRepository;
import itda.ieoso.Response.Response;
import itda.ieoso.Video.VideoDto;
import itda.ieoso.Video.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/materials")
public class MaterialController {
    @Autowired
    private MaterialService materialService;
    @Autowired
    private S3Service s3Service;



    @PostMapping("/{courseId}/{lectureId}/{userId}")
    public Response<MaterialDto.Response> createMaterial(@PathVariable Long courseId,
                                                         @PathVariable Long lectureId,
                                                         @PathVariable Long userId) {
        return Response.success("강의자료 생성", materialService.createMaterial(courseId, lectureId, userId));
    }

    @PatchMapping("/{courseId}/{materialId}/{userId}")
    public Response<MaterialDto.Response> updateMaterial(@PathVariable Long courseId,
                                                         @PathVariable Long materialId,
                                                         @PathVariable Long userId,
                                                         @RequestParam(value = "materialTitle", required = false) String materialTitle,
                                                         @RequestParam(value = "file", required = false) MultipartFile file,
                                                         @RequestParam(value = "startDate", required = false) LocalDateTime startDate) {
        return Response.success("강의자료 수정", materialService.updateMaterial(courseId, materialId, userId, materialTitle, file, startDate));
    }

    @DeleteMapping("/{courseId}/{materialId}/{userId}")
    public Response<MaterialDto.deleteResponse> deleteMaterial(@PathVariable Long courseId,
                                               @PathVariable Long materialId,
                                               @PathVariable Long userId) {
        return Response.success("강의자료 삭제", materialService.deleteMaterial(courseId,materialId,userId));
    }

    @GetMapping("/download")
    public Response<String> getMaterialDownloadUrl(@RequestParam("fileUrl") String fileUrl, @RequestParam("materialId") Long materialId) {
        try {
            System.out.println("fileUrl: " + fileUrl);
            // fileUrl에서 S3 도메인 부분 제거 후 key만 추출
            String fileKey = fileUrl.replace("https://your-s3-bucket.s3.amazonaws.com/", "");

            // Presigned URL 생성
            String presignedUrl = s3Service.generatePresignedUrl(fileKey);

            materialService.updateMaterialHistoryStatus(materialId);

            return Response.success("강의 자료 다운로드", presignedUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
