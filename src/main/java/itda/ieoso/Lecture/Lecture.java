package itda.ieoso.Lecture;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.Course.Course;
import itda.ieoso.Material.Material;
import itda.ieoso.Video.Video;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")
    private Long lectureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 200)
    private String lectureTitle;

    @Column(length = 500)
    private String lectureDescription;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;


    // TODO 나중에 관리를 위해 양방향 매핑시 joincolumn 추가
    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Material> materials = new ArrayList<>();

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Video> videos = new ArrayList<>();

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Assignment> assignments = new ArrayList<>();

    // 생성자: 모든 필드를 초기화
    @Builder
    public Lecture(Course course, String lectureTitle, String lectureDescription,
                   LocalDate startDate, LocalDate endDate) {
        this.course = course;
        this.lectureTitle = lectureTitle;
        this.lectureDescription = lectureDescription;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setLectureTitle(String lectureTitle) {
        this.lectureTitle = lectureTitle;
    }

    public void setLectureDescription(String lectureDescription) {
        this.lectureDescription = lectureDescription;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
