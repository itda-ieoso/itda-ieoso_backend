package itda.ieoso.Material;

import itda.ieoso.ContentOrder.ContentOrder;
import itda.ieoso.Video.Video;
import itda.ieoso.Video.VideoDto;
import itda.ieoso.VideoHistory.VideoHistoryStatus;
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
    }

    //@JsonInclude(JsonInclude.Include.NON_NULL)
    public record ToDoResponse(
            Long materialId,
            String materialTitle,
            String MaterialFile,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String originalFilename,
            Boolean materialHistoryStatus,
            Long contentOrderId,
            String contentType,
            Integer contentOrderIndex

    ) {
        public static MaterialDto.ToDoResponse of(Material material, Boolean materialHistoryStatus, ContentOrder contentOrder) {
            return new MaterialDto.ToDoResponse(
                    material.getMaterialId(),
                    material.getMaterialTitle(),
                    material.getMaterialFile(),
                    material.getStartDate(),
                    material.getEndDate(),
                    material.getOriginalFilename(),
                    materialHistoryStatus,
                    contentOrder.getContentOrderId(),
                    contentOrder.getContentType(),
                    contentOrder.getOrderIndex()
            );
        }
    }

    @Builder
    public record deleteResponse(
            Long materialId,
            String message
    ) {}
}
