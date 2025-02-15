package itda.ieoso.Assignment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class AssignmentDTO {
    public record Request(
            String assignmentTitle,
            String assignmentDescription,
            LocalDateTime startDate,
            LocalDateTime endDate

    ) {}

    public record Response(
            Long assignmentId,
            String assignmentTitle,
            String assignmentDescription,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        public static Response of(Assignment assignment) {
            return new Response(
                    assignment.getAssignmentId(),
                    assignment.getAssignmentTitle(),
                    assignment.getAssignmentDescription(),
                    assignment.getStartDate(),
                    assignment.getEndDate()
            );
        }
    }

    @Builder
    public record deleteResponse(
            Long assignmentId,
            String message
    ) {}

}



