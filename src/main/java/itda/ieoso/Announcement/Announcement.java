package itda.ieoso.Announcement;

import itda.ieoso.Course.Course;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "announcement_id", nullable = false)
    private Long announcementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 200)
    private String announcementTitle;

    @Column(columnDefinition = "TEXT")
    private String announcementContent;

    @Column
    private int viewCount;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    public void setAnnouncementTitle(String announcementTitle) {
        this.announcementTitle = announcementTitle;
    }

    public void setAnnouncementContent(String announcementContent) {
        this.announcementContent = announcementContent;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateViewCount() {
        this.viewCount +=1;
    }
}
