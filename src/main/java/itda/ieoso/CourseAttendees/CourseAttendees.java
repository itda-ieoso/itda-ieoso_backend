package itda.ieoso.CourseAttendees;

import itda.ieoso.Course.Course;
import itda.ieoso.User.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Entity
@Getter
public class CourseAttendees {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_attendees_id", nullable = false)
    private Long courseAttendeesId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp joinedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "course_attendees_status", nullable = false)
    private CourseAttendeesStatus courseAttendeesStatus;

    @Builder
    public CourseAttendees(Course course, User user, Timestamp joinedAt, CourseAttendeesStatus courseAttendeesStatus) {
        this.course = course;
        this.user = user;
        this.joinedAt = joinedAt;
        this.courseAttendeesStatus = courseAttendeesStatus;
    }

}
