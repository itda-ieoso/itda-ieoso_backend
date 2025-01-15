package itda.ieoso.Course;

import itda.ieoso.Lecture.Lecture;
import itda.ieoso.User.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Course {

    @Id
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

    // 강의를 추가하는 메서드
//    public void addLecture(Lecture lecture) {
//        lectures.add(lecture);
//        lecture.setCourse(this);
//    }
//
//    // 강의를 삭제하는 메서드
//    public void removeLecture(Lecture lecture) {
//        lectures.remove(lecture);
//        lecture.setCourse(null);
//    }

}
