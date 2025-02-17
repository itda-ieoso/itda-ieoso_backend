package itda.ieoso.Material;

import itda.ieoso.File.S3Service;
import itda.ieoso.Response.Response;
import itda.ieoso.Video.VideoDto;
import itda.ieoso.Video.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/materials")
public class MaterialController {
    @Autowired
    private MaterialService materialService;
    private S3Service s3Service;

    @PostMapping("/{courseId}/{lectureId}/{userId}")
    public Response<MaterialDto.Response> createMaterial(@PathVariable Long courseId,
                                                         @PathVariable Long lectureId,
                                                         @PathVariable Long userId,
                                                         @RequestParam("materialTitle") String materialTitle,
                                                         @RequestParam(value = "file", required = false) MultipartFile file,
                                                         @RequestParam("startDate") LocalDate startDate) {
        return Response.success("강의자료 생성", materialService.createMaterial(courseId, lectureId, userId, materialTitle, file, startDate));
    }

    @PatchMapping("/{courseId}/{materialId}/{userId}")
    public Response<MaterialDto.Response> updateMaterial(@PathVariable Long courseId,
                                                         @PathVariable Long materialId,
                                                         @PathVariable Long userId,
                                                         @RequestParam(value = "materialTitle", required = false) String materialTitle,
                                                         @RequestParam(value = "file", required = false) MultipartFile file,
                                                         @RequestParam(value = "startDate", required = false) LocalDate startDate) {
        return Response.success("강의자료 수정", materialService.updateMaterial(courseId, materialId, userId, materialTitle, file, startDate));
    }

    @DeleteMapping("/{courseId}/{materialId}/{userId}")
    public Response<MaterialDto.deleteResponse> deleteMaterial(@PathVariable Long courseId,
                                               @PathVariable Long materialId,
                                               @PathVariable Long userId) {
        return Response.success("강의자료 삭제", materialService.deleteMaterial(courseId,materialId,userId));
    }

}
