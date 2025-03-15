package itda.ieoso.Announcement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

public class AnnouncementDto {
    public record Request (
            String title,
            String content
    ) {}

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Response (
            Long announcementId,
            String instructorName,
            String announcementTitle,
            String announcementContent,
            int viewCount,
            LocalDateTime createdAt
    ) {}

    @Builder
    public record deleteResponse(
            Long id,
            String message
    ) {}

}
