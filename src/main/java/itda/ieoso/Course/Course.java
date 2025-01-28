package itda.ieoso.Course;

import itda.ieoso.Lecture.Lecture;
import itda.ieoso.User.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private int maxStudents;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDate closedDate;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private String courseThumbnail;

    @Column(unique = true)
    private String entryCode;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lecture> lectures = new ArrayList<>();

    // 생성자 수정: 이제 모든 필드를 받음
    @Builder
    public Course(User user, String courseTitle, String courseDescription, int maxStudents, LocalDate closedDate,
                  String courseThumbnail, String entryCode) {
        this.user = user;
        this.courseTitle = courseTitle;
        this.courseDescription = courseDescription;
        this.maxStudents = maxStudents;
        this.closedDate = closedDate;
        this.courseThumbnail = courseThumbnail;
        this.entryCode = entryCode;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setEntryCode(String entryCode) {
        this.entryCode = entryCode;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public void setMaxStudents(int maxStudents) {
        this.maxStudents = maxStudents;
    }

    public void setClosedDate(LocalDate closedDate) {
        this.closedDate = closedDate;
    }

    public void setCourseThumbnail(String courseThumbnail) {
        this.courseThumbnail = courseThumbnail;
    }
}
