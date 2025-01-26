package itda.ieoso.Video;

import itda.ieoso.Lecture.Lecture;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private Long videoId;

    private String videoTitle;
    private String videoUrl;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

}
