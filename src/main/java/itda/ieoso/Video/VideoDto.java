package itda.ieoso.Video;

import lombok.Builder;

import java.time.LocalDateTime;

public class VideoDto {
    public record Request(
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
    ) {
        public static Response of(Video video) {
            return new Response(
                    video.getVideoId(),
                    video.getVideoTitle(),
                    video.getVideoUrl(),
                    video.getStartDate(),
                    video.getEndDate()
            );
        }
    }

    @Builder
    public record deleteResponse(
            Long videoId,
            String message
    ) {}
}
