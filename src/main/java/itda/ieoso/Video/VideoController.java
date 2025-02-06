package itda.ieoso.Video;

import itda.ieoso.Response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("videos")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @PostMapping("/{courseId}/{lectureId}/{userId}")
    public Response<VideoDto.Response> createVideo(@PathVariable Long courseId,
                                                  @PathVariable Long lectureId,
                                                  @PathVariable Long userId,
                                                  @RequestBody VideoDto.createRequest request) {

        return Response.success("강의영상 생성", videoService.createVideo(courseId,lectureId,userId,request));
    }

    @PatchMapping("/{courseId}/{videoId}/{userId}")
    public Response<VideoDto.Response> updateVideo(@PathVariable Long courseId,
                                         @PathVariable Long videoId,
                                         @PathVariable Long userId,
                                         @RequestBody VideoDto.updateRequest request) {
        return Response.success("강의영상 수정", videoService.updateVideo(courseId,videoId,userId,request));
    }

    @DeleteMapping("/{courseId}/{videoId}/{userId}")
    public Response<VideoDto.deleteResponse> deleteVideo(@PathVariable Long courseId,
                                               @PathVariable Long videoId,
                                               @PathVariable Long userId) {
        return Response.success("강의영상 삭제", videoService.deleteVideo(courseId,videoId,userId));
    }



}
