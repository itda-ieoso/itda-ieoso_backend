package itda.ieoso.MaterialHistory;

import itda.ieoso.VideoHistory.VideoHistory;
import itda.ieoso.VideoHistory.VideoHistoryDto;
import itda.ieoso.VideoHistory.VideoHistoryStatus;

import java.time.LocalDateTime;

public class MaterialHistoryDto {
    public record Response(
            Long materialId,
            Long materialHistoryId,
            boolean materialHistoryStatus
    ) {
        public static Response of(MaterialHistory materialHistory) {
            return new Response(
                    materialHistory.getMaterial().getMaterialId(),
                    materialHistory.getMaterialHistoryId(),
                    materialHistory.isMaterialHistoryStatus()
            );
        }
    }

    public record ToDoListResponse(
            Long materialId,
            String materialTitle,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long materialHistoryId,
            boolean materialHistoryStatus,
            Long lectureId,
            Integer orderIndex
    ) {
        public static ToDoListResponse of(Long materialId, String materialTitle, MaterialHistory history, Integer order) {
            return new ToDoListResponse (
                    materialId,
                    materialTitle,
                    history.getMaterial().getStartDate(),
                    history.getMaterial().getEndDate(),
                    history.getMaterialHistoryId(),
                    history.isMaterialHistoryStatus(),
                    history.getMaterial().getLecture().getLectureId(),
                    order
            );
        }
    }
}
