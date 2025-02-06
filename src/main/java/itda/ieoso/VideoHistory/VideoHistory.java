package itda.ieoso.VideoHistory;

import itda.ieoso.Course.Course;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.Video.Video;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_history_id")
    private Long videoHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "lecture_id", nullable = false)
//    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_attendees_id", nullable = false)
    private CourseAttendees courseAttendees;

    @Enumerated(EnumType.STRING)
    @Column(name = "lecture_status", nullable = false)
    private VideoHistoryStatus videoHistoryStatus;
}
