package itda.ieoso.Course.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CourseOverviewUpdateDto {
    private String description;
    private String courseThumbnail;
}
