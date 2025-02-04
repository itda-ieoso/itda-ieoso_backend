package itda.ieoso.Course;

import itda.ieoso.Lecture.Lecture;
import itda.ieoso.User.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Currency;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String courseTitle;

    @Column(length = 500)
    private String courseDescription;

    @Column
    private String instructorName; // 강의자명(수정가능한 이름)

    @Column
    private LocalDate startDate;

    @Column
    private int durationWeeks; // 코스 진행 기간

    @Column
    private String lectureDay; // 강의요일: 월 1, 화 2, 수 3, 목 4, 금 5, 토 6, 일 7 (여러개 선택가능)

    @Column
    private Time lectureTime; // 강의시간

    @Column
    private String assignmentDueDay; // 과제제출요일: 월 1, 화 2, 수 3, 목 4, 금 5, 토 6, 일 7(여러개선택가능)

    @Column
    private Time assignmentDueTime; // 과제제출시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficultyLevel;

    public enum DifficultyLevel {
        EASY,
        MEDIUM,
        HARD;
    }

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private String courseThumbnail;

    @Column(unique = true)
    private String entryCode;

    @Column
    private boolean init; // 초기설정 여부

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lecture> lectures = new ArrayList<>();

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public void setCourseThumbnail(String courseThumbnail) {
        this.courseThumbnail = courseThumbnail;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public void setLectureDay(String lectureDay) {
        this.lectureDay = lectureDay;
    }

    public void setLectureTime(Time lectureTime) {
        this.lectureTime = lectureTime;
    }

    public void setAssignmentDueDay(String assignmentDueDay) {
        this.assignmentDueDay = assignmentDueDay;
    }

    public void setAssignmentDueTime(Time assignmentDueTime) {
        this.assignmentDueTime = assignmentDueTime;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public void setDurationWeeks(int durationWeeks) {
        this.durationWeeks = durationWeeks;
    }

    public void updateInit() {
        this.init = true;
    }
}
