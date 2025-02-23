package itda.ieoso.Assignment;

import itda.ieoso.ContentOrder.ContentOrder;
import itda.ieoso.Submission.SubmissionStatus;
import itda.ieoso.Video.Video;
import itda.ieoso.Video.VideoDto;
import itda.ieoso.VideoHistory.VideoHistoryStatus;
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
            // LocalDateTime startDate,
            LocalDateTime endDate

    ) {}

    public record Response(
            Long assignmentId,
            String assignmentTitle,
            String assignmentDescription,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long contentOrderId,
            String contentType,
            Integer contentOrderIndex
    ) {
        public static Response of(Assignment assignment) {
            return new Response(
                    assignment.getAssignmentId(),
                    assignment.getAssignmentTitle(),
                    assignment.getAssignmentDescription(),
                    assignment.getStartDate(),
                    assignment.getEndDate(),
                    null,
                    null,
                    null
            );
        }

        public static Response of(Assignment assignment, ContentOrder contentOrder) {
            return new Response(
                    assignment.getAssignmentId(),
                    assignment.getAssignmentTitle(),
                    assignment.getAssignmentDescription(),
                    assignment.getStartDate(),
                    assignment.getEndDate(),
                    contentOrder.getContentOrderId(),
                    contentOrder.getContentType(),
                    contentOrder.getOrderIndex()
            );
        }
    }

    //@JsonInclude(JsonInclude.Include.NON_NULL)
    public record ToDoResponse(
            Long assignmentId,
            String assignmentTitle,
            String getAssignmentDescription,
            LocalDateTime startDate,
            LocalDateTime endDate,
            SubmissionStatus submissionStatus,
            Long contentOrderId,
            String contentType,
            Integer contentOrderIndex

    ) {
        public static AssignmentDTO.ToDoResponse of(Assignment assignment, SubmissionStatus submissionStatus, ContentOrder contentOrder) {
            return new AssignmentDTO.ToDoResponse(
                    assignment.getAssignmentId(),
                    assignment.getAssignmentTitle(),
                    assignment.getAssignmentDescription(),
                    assignment.getStartDate(),
                    assignment.getEndDate(),
                    submissionStatus,
                    contentOrder.getContentOrderId(),
                    contentOrder.getContentType(),
                    contentOrder.getOrderIndex()
            );
        }
    }

    @Builder
    public record deleteResponse(
            Long assignmentId,
            String message
    ) {}

}



