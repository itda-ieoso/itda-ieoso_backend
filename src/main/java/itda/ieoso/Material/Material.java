package itda.ieoso.Material;

import itda.ieoso.Course.Course;
import itda.ieoso.Lecture.Lecture;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    private Long materialId;

    private String materialTitle;
    private String materialFile;

    @ManyToOne
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<MaterialHistory> materialHistories = new ArrayList<>();


    public void setMaterialTitle(String materialTitle) {
        this.materialTitle = materialTitle;
    }

    public void setMaterialFile(String materialFile) {
        this.materialFile = materialFile;
    }
}
