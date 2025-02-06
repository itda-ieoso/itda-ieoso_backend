package itda.ieoso.Material;

import itda.ieoso.Response.Response;
import itda.ieoso.Video.VideoDto;
import itda.ieoso.Video.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/materials")
public class MaterialController {
    @Autowired
    private MaterialService materialService;

    @PostMapping("/{courseId}/{lectureId}/{userId}")
    public Response<MaterialDto.Response> createMaterial(@PathVariable Long courseId,
                                                        @PathVariable Long lectureId,
                                                        @PathVariable Long userId,
                                                        @RequestBody MaterialDto.createRequest request) {

        return Response.success("강의자료 생성", materialService.createMaterial(courseId,lectureId,userId,request));
    }

    @PatchMapping("/{courseId}/{materialId}/{userId}")
    public Response<MaterialDto.Response> updateMaterial(@PathVariable Long courseId,
                                         @PathVariable Long materialId,
                                         @PathVariable Long userId,
                                         @RequestBody MaterialDto.updateRequest request) {
        return Response.success("강의자료 수정", materialService.updateMaterial(courseId,materialId,userId,request));
    }

    @DeleteMapping("/{courseId}/{materialId}/{userId}")
    public Response<MaterialDto.deleteResponse> deleteMaterial(@PathVariable Long courseId,
                                               @PathVariable Long materialId,
                                               @PathVariable Long userId) {
        return Response.success("강의자료 삭제", materialService.deleteMaterial(courseId,materialId,userId));
    }

}
