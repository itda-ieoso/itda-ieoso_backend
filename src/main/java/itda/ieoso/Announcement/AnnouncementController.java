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

    // 강의 공지 생성(개설자만)
    @PostMapping("/{courseId}/{userId}")
    public Response<AnnouncementDto.Response> createAnnouncement(@PathVariable Long courseId,
                                                                @PathVariable Long userId,
                                                                @RequestBody AnnouncementDto.Request responseDto, @RequestHeader("Authorization") String token) {
        return Response.success("강의공지 생성", announcementService.createAnnouncement(courseId, token, responseDto));
    }

    // 강의공지 업데이트(개설자만)
    @PatchMapping("/{courseId}/{userId}/{announcementId}")
    public Response<AnnouncementDto.Response> updateAnnouncement(@PathVariable Long courseId,
                                                                @PathVariable Long userId,
                                                                @PathVariable Long announcementId,
                                                                @RequestBody AnnouncementDto.Request responseDto, @RequestHeader("Authorization") String token) {

        return Response.success("강의공지 수정", announcementService.updateAnnouncement(courseId, token, announcementId, responseDto));
    }

    // 강의공지 삭제(개설자만)
    @DeleteMapping("/{courseId}/{userId}/{announcementId}")
    public Response<AnnouncementDto.deleteResponse> deleteAnnouncement(@PathVariable Long courseId,
                                          @PathVariable Long userId,
                                          @PathVariable Long announcementId, @RequestHeader("Authorization") String token) {

        announcementService.deleteAnnouncement(courseId, token, announcementId);

        return Response.success("강의공지 삭제", null);
    }


    // 강의공지 목록 조회(개설자, 수강생) (userid는 courseattendees검증용)
    @GetMapping("/{courseId}/{userId}")
    public Response<List<AnnouncementDto.Response>> getAnnouncements(@PathVariable Long courseId, @PathVariable Long userId, @RequestHeader("Authorization") String token) {
        return Response.success("강의공지 목록 조회", announcementService.getAnnouncements(courseId, token));
    }

    // 강의공지 상세 조회(개설자, 수강생) (userid는 courseattendees검증용)
    @GetMapping("/{courseId}/{userId}/{announcementId}")
    public Response<AnnouncementDto.Response> getAnnouncement(@PathVariable Long courseId,
                                @PathVariable Long userId,
                                @PathVariable Long announcementId, @RequestHeader("Authorization") String token) {

        return Response.success("강의공지 상세 조회", announcementService.getAnnouncement(courseId, token, announcementId));
    }

}
