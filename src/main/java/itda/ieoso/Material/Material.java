package itda.ieoso.Material;

import itda.ieoso.Lecture.Lecture;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    private Long materialId;

    private String materialTitle;
    private String materialFile;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;
}
