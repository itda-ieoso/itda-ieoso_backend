package itda.ieoso.ContentOrder;

import itda.ieoso.Course.Course;
import itda.ieoso.Lecture.Lecture;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ContentOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentOrderId;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // 같은 lecture 내에서 정렬
    @ManyToOne
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    // type : video, material, assignment
    private String contentType;

    // type의 실제 id값
    private Long contentId;

    // Fractional Indexing
    private int orderIndex;

    public ContentOrder(Course course, Lecture lecture, String contentType, Long contentId, int orderIndex) {
        this.course = course;
        this.lecture = lecture;
        this.contentType = contentType;
        this.contentId = contentId;
        this.orderIndex = orderIndex;

    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }
}
