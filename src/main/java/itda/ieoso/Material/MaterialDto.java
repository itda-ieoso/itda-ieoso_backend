package itda.ieoso.Material;

import lombok.Builder;

import java.time.LocalDateTime;

public class MaterialDto {
    public record createRequest(
            String materialTitle,
            String materialFile
    ) {}

    public record updateRequest(
            String materialTitle,
            String materialFile
    ) {}

    @Builder
    public record Response(
            Long materialId,
            String materialTitle,
            String materialFile
    ) {}

    @Builder
    public record deleteResponse(
            Long videoId,
            String message
    ) {}
}
