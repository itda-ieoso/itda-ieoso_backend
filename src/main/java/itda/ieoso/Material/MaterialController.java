package itda.ieoso.Material;

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
    public MaterialDto.Response createMaterial(@PathVariable Long courseId,
                                         @PathVariable Long lectureId,
                                         @PathVariable Long userId,
                                         @RequestBody MaterialDto.createRequest request) {

        return materialService.createMaterial(courseId,lectureId,userId,request);
    }

    @PatchMapping("/{courseId}/{materialId}/{userId}")
    public MaterialDto.Response updateMaterial(@PathVariable Long courseId,
                                         @PathVariable Long materialId,
                                         @PathVariable Long userId,
                                         @RequestBody MaterialDto.updateRequest request) {
        return materialService.updateMaterial(courseId,materialId,userId,request);
    }

    @DeleteMapping("/{courseId}/{materialId}/{userId}")
    public MaterialDto.deleteResponse deleteMaterial(@PathVariable Long courseId,
                                               @PathVariable Long materialId,
                                               @PathVariable Long userId) {
        return materialService.deleteMaterial(courseId,materialId,userId);
    }

}
