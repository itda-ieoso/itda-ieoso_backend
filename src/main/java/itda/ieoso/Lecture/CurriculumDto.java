package itda.ieoso.Lecture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class CurriculumDto {

    // lecture
    private String lectureTitle;
    private String lectureDescription;

    private List<VideoDto> videos;
    private List<MaterialDto> materials;
    private List<AssignmentDto> assignments;

    private LocalDate startDate;
    private LocalDate endDate;


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MaterialDto {
        private String materialTitle;
        private String materialFile;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignmentDto {
        private String assignmentTitle;
        private String assignmentDescription;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VideoDto {
        private String videoTitle;
        private String videoUrl;
        private LocalDate startDate;
        private LocalDate endDate;
    }

}
