package itda.ieoso.Course;

import itda.ieoso.Course.Course.DifficultyLevel;
import itda.ieoso.User.UserDTO;
import lombok.Builder;
import lombok.Getter;

import java.sql.Time;
import java.time.LocalDate;

@Getter
@Builder
public class CourseDTO {
    private Long courseId;
    private String courseTitle;
    private String courseDescription;
    private String instructorName;
    private LocalDate startDate;
    private int durationWeeks;
    private String lectureDay;
    private Time lectureTime;
    private String assignmentDueDay;
    private Time assignmentDueTime;
    private DifficultyLevel difficultyLevel;
    private String courseThumbnail;
    private String entryCode;
    private boolean init; // 초기설정 여부
    private UserDTO.UserInfoDto user;  // User 정보를 UserDTO로 변경

    // Course와 UserDTO를 합쳐서 반환
    public static CourseDTO of(Course course, UserDTO.UserInfoDto userInfoDto) {
        return CourseDTO.builder()
                .courseId(course.getCourseId())
                .courseTitle(course.getCourseTitle())
                .courseDescription(course.getCourseDescription())
                .instructorName(course.getInstructorName())
                .startDate(course.getStartDate())
                .durationWeeks(course.getDurationWeeks())
                .lectureDay(course.getLectureDay())
                .lectureTime(course.getLectureTime())
                .assignmentDueDay(course.getAssignmentDueDay())
                .assignmentDueTime(course.getAssignmentDueTime())
                .difficultyLevel(course.getDifficultyLevel())
                .courseThumbnail(course.getCourseThumbnail())
                .entryCode(course.getEntryCode())
                .init(course.isInit())
                .user(userInfoDto)
                .build();
    }
}
