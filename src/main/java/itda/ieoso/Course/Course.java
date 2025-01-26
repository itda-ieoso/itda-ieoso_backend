package itda.ieoso.Course;

import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.Lecture.Lecture;
import itda.ieoso.User.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
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

    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdAt;

    @Column(nullable = false)
    private LocalDate closedDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp updatedAt;

    @Column
    private String courseThumbnail;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lecture> lectures = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseAttendees> courseAttendees = new ArrayList<>();

    // 기본 생성자 추가
    public Course() {
    }

    // 생성자: 필수 값만 받아서 객체 초기화
    public Course(User user, String courseTitle, String courseDescription,
                  int maxStudents, LocalDate closedDate) {
        this.user = user;
        this.courseTitle = courseTitle;
        this.courseDescription = courseDescription;
        this.maxStudents = maxStudents;
        this.closedDate = closedDate;
    }
    public void setCreatedAt(LocalDateTime now) {
    }

    public void setUpdatedAt(LocalDateTime now) {
    }
}
