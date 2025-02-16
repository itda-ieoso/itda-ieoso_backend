package itda.ieoso.Video;

import com.fasterxml.jackson.annotation.JsonInclude;
import itda.ieoso.ContentOrder.ContentOrder;
import lombok.Builder;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
            LocalDateTime endDate,
            Long contentOrderId,
            String contentType,
            Integer contentOrderIndex
    ) {
        public static Response of(Video video, ContentOrder contentOrder) {
            return new Response(
                    video.getVideoId(),
                    video.getVideoTitle(),
                    video.getVideoUrl(),
                    video.getStartDate(),
                    video.getEndDate(),
                    contentOrder.getContentOrderId(),
                    contentOrder.getContentType(),
                    contentOrder.getOrderIndex()
            );
        }

        public static Response of(Video video) {
            return new Response(
                    video.getVideoId(),
                    video.getVideoTitle(),
                    video.getVideoUrl(),
                    video.getStartDate(),
                    video.getEndDate(),
                    null,   // contentOrderId가 없으므로 null
                    null,   // contentType이 없으므로 null
                    null    // contentOrderIndex가 없으므로 null
            );
        }
    }

    @Builder
    public record deleteResponse(
            Long videoId,
            String message
    ) {}
}
