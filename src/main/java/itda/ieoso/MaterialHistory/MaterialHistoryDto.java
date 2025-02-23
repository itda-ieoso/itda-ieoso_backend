package itda.ieoso.MaterialHistory;


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
}
