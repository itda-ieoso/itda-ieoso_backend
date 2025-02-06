package itda.ieoso.Video;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("videos")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @PostMapping("/{courseId}/{lectureId}/{userId}")
    public VideoDto.Response createVideo(@PathVariable Long courseId,
                                         @PathVariable Long lectureId,
                                         @PathVariable Long userId,
                                         @RequestBody VideoDto.createRequest request) {

        return videoService.createVideo(courseId,lectureId,userId,request);
    }

    @PatchMapping("/{courseId}/{videoId}/{userId}")
    public VideoDto.Response updateVideo(@PathVariable Long courseId,
                                         @PathVariable Long videoId,
                                         @PathVariable Long userId,
                                         @RequestBody VideoDto.updateRequest request) {
        return videoService.updateVideo(courseId,videoId,userId,request);
    }

    @DeleteMapping("/{courseId}/{videoId}/{userId}")
    public VideoDto.deleteResponse deleteVideo(@PathVariable Long courseId,
                                               @PathVariable Long videoId,
                                               @PathVariable Long userId) {
        return videoService.deleteVideo(courseId,videoId,userId);
    }



}
