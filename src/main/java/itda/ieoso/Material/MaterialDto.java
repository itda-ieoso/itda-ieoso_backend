package itda.ieoso.Material;

import itda.ieoso.ContentOrder.ContentOrder;
import lombok.Builder;

import java.time.LocalDate;
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
            String fileSize,
            String originalFilename,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long contentOrderId,
            String contentType,
            Integer contentOrderIndex
    ) {
        public static Response of(Material material, ContentOrder contentOrder) {
            return new Response(
                    material.getMaterialId(),
                    material.getMaterialTitle(),
                    material.getMaterialFile(),
                    material.getFileSize(),
                    material.getOriginalFilename(),
                    material.getStartDate(),
                    material.getEndDate(),
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
                    material.getFileSize(),
                    material.getOriginalFilename(),
                    material.getStartDate(),
                    material.getEndDate(),
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
