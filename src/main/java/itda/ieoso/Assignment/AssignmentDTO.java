package itda.ieoso.Assignment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class AssignmentDTO {
    public record createRequest(
            String assignmentTitle,
            String assignmentDescription,
            LocalDateTime startDate,
            LocalDateTime endDate

    ) {}

    public record updateRequest(
            String assignmentTitle,
            String assignmentDescription,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {}

    @Builder
    public record Response(
            Long assignmentId,
            String assignmentTitle,
            String assignmentDescription,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {}

    @Builder
    public record deleteResponse(
            Long assignmentId,
            String message
    ) {}

}



