package itda.ieoso.Announcement;

import itda.ieoso.Response.BasicResponse;
import itda.ieoso.Response.DataResponse;
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
    public DataResponse<AnnouncementResponseDto> createAnnouncement(@PathVariable Long courseId,
                                                                    @PathVariable Long userId,
                                                                    @RequestBody AnnouncementRequestDto responseDto) {
        DataResponse<AnnouncementResponseDto> response = new DataResponse<>(announcementService.createAnnouncement(courseId, userId, responseDto));
        return response;
    }

    @GetMapping("/{courseId}/{userId}")
    public DataResponse<List<AnnouncementResponseDto>> getAnnouncements(@PathVariable Long courseId, @PathVariable Long userId) {
        DataResponse<List<AnnouncementResponseDto>> response
                = new DataResponse<>(announcementService.getAnnouncements(courseId, userId));
        return response;
    }

    @GetMapping("/{courseId}/{userId}/{announcementId}")
    public DataResponse<AnnouncementResponseDto> getAnnouncement(@PathVariable Long courseId,
                                @PathVariable Long userId,
                                @PathVariable Long announcementId) {

        DataResponse<AnnouncementResponseDto> response
                = new DataResponse<>(announcementService.getAnnouncement(courseId, userId, announcementId));
        return response;
    }

    @PatchMapping("/{courseId}/{userId}/{announcementId}")
    public DataResponse<AnnouncementResponseDto> updateAnnouncement(@PathVariable Long courseId,
                                                                    @PathVariable Long userId,
                                                                    @PathVariable Long announcementId,
                                                                    @RequestBody AnnouncementRequestDto responseDto) {

        DataResponse<AnnouncementResponseDto> response = new DataResponse<>
                (announcementService.updateAnnouncement(courseId, userId, announcementId, responseDto));
        return response;
    }

    @DeleteMapping("/{courseId}/{userId}/{announcementId}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long courseId,
                                            @PathVariable Long userId,
                                            @PathVariable Long announcementId) {

        announcementService.deleteAnnouncement(courseId, userId, announcementId);

        return ResponseEntity.noContent().build();
    }
}
