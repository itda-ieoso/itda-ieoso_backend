package itda.ieoso.Announcement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnnouncementResponseDto {
    private Long announcementId;
    private String instructorName;
    private String announcementTitle;
    private String announcementContent;
    private LocalDateTime createdAt;

    public static AnnouncementResponseDto of(Announcement announcement) {
        AnnouncementResponseDto responseDto = AnnouncementResponseDto.builder()
                .announcementId(announcement.getAnnouncementId())
                .instructorName(announcement.getCourse().getInstructorName())
                .announcementTitle(announcement.getAnnouncementTitle())
                .announcementContent(announcement.getAnnouncementContent())
                .createdAt(announcement.getCreatedAt())
                .build();

        return responseDto;
    }

    public static AnnouncementResponseDto summary(Announcement announcement) {
        AnnouncementResponseDto responseDto = AnnouncementResponseDto.builder()
                .announcementId(announcement.getAnnouncementId())
                .instructorName(announcement.getCourse().getInstructorName())
                .announcementTitle(announcement.getAnnouncementTitle())
                .announcementContent(null)
                .createdAt(announcement.getCreatedAt())
                .build();

        return responseDto;
    }
}
