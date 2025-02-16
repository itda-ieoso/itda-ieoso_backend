package itda.ieoso.Material;

import itda.ieoso.Course.Course;
import itda.ieoso.Lecture.Lecture;
import itda.ieoso.MaterialHistory.MaterialHistory;
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
    private String originalFilename; // 원본 파일 이름
    private String fileSize;        // 파일 크기

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

    // 원본 파일 이름 수정
    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    // 파일 크기 수정
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
}
