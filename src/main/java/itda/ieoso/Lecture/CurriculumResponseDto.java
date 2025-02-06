package itda.ieoso.Lecture;

import itda.ieoso.Submission.SubmissionStatus;
import itda.ieoso.VideoHistory.VideoHistoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumResponseDto {
    private Long lectureId;
    private String lectureTitle;
    private String lectureDescription;

    private List<VideoResponseDto> videos;
    private List<MaterialResponseDto> materials;
    private List<AssignmentResponseDto> assignments;

    private LocalDate startDate;
    private LocalDate endDate;


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MaterialResponseDto {
        private Long materialId;
        private String materialTitle;
        private String materialFile;
        private boolean materialHistoryStatus;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssignmentResponseDto {
        private Long assignmentId;
        private String assignmentTitle;
        private String assignmentDescription;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private SubmissionStatus submissionStatus;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class VideoResponseDto {
        private Long videoId;
        private String videoTitle;
        private String videoUrl;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private VideoHistoryStatus videoHistoryStatus;
    }
}
