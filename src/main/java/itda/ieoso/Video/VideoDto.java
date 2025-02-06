package itda.ieoso.Video;

import lombok.Builder;

import java.time.LocalDateTime;

public class VideoDto {
    public record createRequest(
            String videoTitle,
            String videoUrl,
            LocalDateTime startDate,
            LocalDateTime endDate

    ) {}

    public record updateRequest(
            String videoTitle,
            String videoUrl,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {}

    @Builder
    public record Response(
            Long videoId,
            String videoTitle,
            String videoUrl,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {}

    @Builder
    public record deleteResponse(
            Long id,
            String message
    ) {}
}
