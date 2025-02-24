package itda.ieoso.MaterialHistory;


import itda.ieoso.Course.Course;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.Material.Material;
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
public class MaterialHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_history_id")
    private Long materialHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "lecture_id", nullable = false)
//    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_attendees_id", nullable = false)
    private CourseAttendees courseAttendees;

    private boolean materialHistoryStatus; // false = 다운x / true = 다운o

    public void setMaterialHistoryStatus(boolean materialHistoryStatus) {
        this.materialHistoryStatus = materialHistoryStatus;
    }
}
