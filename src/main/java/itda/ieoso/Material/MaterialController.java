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

    @PatchMapping("/{courseId}/{videoId}/{userId}")
    public MaterialDto.Response updateMaterial(@PathVariable Long courseId,
                                         @PathVariable Long videoId,
                                         @PathVariable Long userId,
                                         @RequestBody MaterialDto.updateRequest request) {
        return materialService.updateMaterial(courseId,videoId,userId,request);
    }

    @DeleteMapping("/{courseId}/{videoId}/{userId}")
    public MaterialDto.deleteResponse deleteMaterial(@PathVariable Long courseId,
                                               @PathVariable Long videoId,
                                               @PathVariable Long userId) {
        return materialService.deleteMaterial(courseId,videoId,userId);
    }

}
