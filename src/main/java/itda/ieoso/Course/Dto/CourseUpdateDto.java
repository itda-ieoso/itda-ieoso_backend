package itda.ieoso.Course.Dto;


import itda.ieoso.Course.Course;
import itda.ieoso.Course.Course.DifficultyLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CourseUpdateDto {
    private String title;
    private String instructorName;
    private LocalDate startDate;
    private int durationWeeks;              // 초기 설정용
    private List<Integer> lectureDay;       // 리스트
    private Time lectureTime;
    private List<Integer> assignmentDueDay; // 리스트
    private Time assignmentDueTime;
    private DifficultyLevel difficultyLevel;

}


