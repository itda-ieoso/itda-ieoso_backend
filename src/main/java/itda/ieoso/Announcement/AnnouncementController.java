package itda.ieoso.Announcement;

import itda.ieoso.Response.BasicResponse;
import itda.ieoso.Response.DataResponse;
import itda.ieoso.Response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @PostMapping("/{courseId}/{userId}")
    public Response<AnnouncementResponseDto> createAnnouncement(@PathVariable Long courseId,
                                                                @PathVariable Long userId,
                                                                @RequestBody AnnouncementRequestDto responseDto) {
        return Response.success("강의공지 생성", announcementService.createAnnouncement(courseId, userId, responseDto));
    }

    @GetMapping("/{courseId}/{userId}")
    public Response<List<AnnouncementResponseDto>> getAnnouncements(@PathVariable Long courseId, @PathVariable Long userId) {
        return Response.success("강의공지 목록 조회", announcementService.getAnnouncements(courseId, userId));
    }

    @GetMapping("/{courseId}/{userId}/{announcementId}")
    public Response<AnnouncementResponseDto> getAnnouncement(@PathVariable Long courseId,
                                @PathVariable Long userId,
                                @PathVariable Long announcementId) {

        return Response.success("강의공지 상세 조회", announcementService.getAnnouncement(courseId, userId, announcementId));
    }

    @PatchMapping("/{courseId}/{userId}/{announcementId}")
    public Response<AnnouncementResponseDto> updateAnnouncement(@PathVariable Long courseId,
                                                                    @PathVariable Long userId,
                                                                    @PathVariable Long announcementId,
                                                                    @RequestBody AnnouncementRequestDto responseDto) {

        return Response.success("강의공지 수정", announcementService.updateAnnouncement(courseId, userId, announcementId, responseDto));
    }

    @DeleteMapping("/{courseId}/{userId}/{announcementId}")
    public Response<?> deleteAnnouncement(@PathVariable Long courseId,
                                            @PathVariable Long userId,
                                            @PathVariable Long announcementId) {

        announcementService.deleteAnnouncement(courseId, userId, announcementId);

        return Response.success("강의공지 삭제", null);
    }
}
