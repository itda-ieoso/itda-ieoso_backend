package itda.ieoso.Assignment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class AssignmentDTO {
    private Long assignmentId;
    private String assignmentTitle;
    private String assignmentDescription;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AssignmentDTO of(Assignment assignment) {
        return AssignmentDTO.builder()
                .assignmentId(assignment.getAssignmentId())
                .assignmentTitle(assignment.getAssignmentTitle())
                .assignmentDescription(assignment.getAssignmentDescription())
                .startDate(assignment.getStartDate())
                .endDate(assignment.getEndDate())
                .createdAt(assignment.getCreatedAt())
                .updatedAt(assignment.getUpdatedAt())
                .build();
    }
}



