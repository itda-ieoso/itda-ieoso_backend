package itda.ieoso.Material;

import lombok.Builder;

import java.time.LocalDateTime;

public class MaterialDto {
    public record Request(
            String materialTitle,
            String materialFile
    ) {}

    public record Response(
            Long materialId,
            String materialTitle,
            String materialFile
    ) {
        public static Response of(Material material) {
            return new Response(
                    material.getMaterialId(),
                    material.getMaterialTitle(),
                    material.getMaterialFile()
            );
        }
    }

    @Builder
    public record deleteResponse(
            Long materialId,
            String message
    ) {}
}
