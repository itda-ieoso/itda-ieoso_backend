package itda.ieoso.Course;

import itda.ieoso.User.UserDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CourseDTO {
    private Long courseId;
    private String courseTitle;
    private String courseDescription;
    private int maxStudents;
    private LocalDate closedDate;
    private String courseThumbnail;
    private String entryCode;
    private UserDTO.UserInfoDto user;  // User 정보를 UserDTO로 변경

    // Course와 UserDTO를 합쳐서 반환
    public static CourseDTO of(Course course, UserDTO.UserInfoDto userInfoDto) {
        return CourseDTO.builder()
                .courseId(course.getCourseId())
                .courseTitle(course.getCourseTitle())
                .courseDescription(course.getCourseDescription())
                .maxStudents(course.getMaxStudents())
                .closedDate(course.getClosedDate())
                .courseThumbnail(course.getCourseThumbnail())
                .entryCode(course.getEntryCode())
                .user(userInfoDto)
                .build();
    }
}
