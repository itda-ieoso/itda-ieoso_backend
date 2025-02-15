package itda.ieoso.ContentOrder;

import itda.ieoso.Course.Course;
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

    // 같은 Course 내에서 정렬
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // type : lecture, video, material, assignment
    private String contentType;

    // type의 실제 id값
    private Long contentId;

    // Fractional Indexing
    private double orderIndex;

    public ContentOrder(Course course, String contentType, Long contentId, double orderIndex) {
        this.course = course;
        this.contentType = contentType;
        this.contentId = contentId;
        this.orderIndex = orderIndex;

    }

    public void setOrderIndex(double orderIndex) {
        this.orderIndex = orderIndex;
    }
}
