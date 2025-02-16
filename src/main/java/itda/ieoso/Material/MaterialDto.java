package itda.ieoso.Material;

import itda.ieoso.ContentOrder.ContentOrder;
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
            String materialFile,
            // int fileSize,
            Long contentOrderId,
            String contentType,
            Integer contentOrderIndex
    ) {
        public static Response of(Material material, ContentOrder contentOrder) {
            return new Response(
                    material.getMaterialId(),
                    material.getMaterialTitle(),
                    material.getMaterialFile(),
                    contentOrder.getContentOrderId(),
                    contentOrder.getContentType(),
                    contentOrder.getOrderIndex()

            );
        }

        public static Response of(Material material) {
            return new Response(
                    material.getMaterialId(),
                    material.getMaterialTitle(),
                    material.getMaterialFile(),
                    null,
                    null,
                    null

            );
        }
    }

    @Builder
    public record deleteResponse(
            Long materialId,
            String message
    ) {}
}
