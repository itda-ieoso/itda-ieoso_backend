package itda.ieoso.CourseAttendees;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Builder
public class CourseAttendeesDTO {
    private Long courseAttendeesId;
    private Long courseId;
    private Long userId;
    private LocalDate joinedAt;
    private String status;

    public static CourseAttendeesDTO of(CourseAttendees courseAttendees) {
        return CourseAttendeesDTO.builder()
                .courseAttendeesId(courseAttendees.getCourseAttendeesId())
                .courseId(courseAttendees.getCourse().getCourseId())
                .userId(courseAttendees.getUser().getUserId())
                .joinedAt(courseAttendees.getJoinedAt())
                .status(courseAttendees.getCourseAttendeesStatus().name())
                .build();
    }
}
