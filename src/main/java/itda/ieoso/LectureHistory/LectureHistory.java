package itda.ieoso.LectureHistory;

import itda.ieoso.Course.Course;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.CourseAttendees.CourseAttendeesStatus;
import itda.ieoso.Lecture.Lecture;
import itda.ieoso.Lecture.LectureStatus;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class LectureHistory {

    @Id
    @Column(name = "lecture_history_id")
    private String lectureHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_attendees_id", nullable = false)
    private CourseAttendees courseAttendees;

    @Enumerated(EnumType.STRING)
    @Column(name = "lecture_status", nullable = false)
    private LectureStatus lectureStatus;
}
