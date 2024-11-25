package itda.ieoso.Lecture.Domain;

import itda.ieoso.Course.Domain.Course;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Lecture {

    @Id
    @Column(name = "lecture_id")
    private String lectureId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String videoLink;

    @Column(nullable = false)
    private Date createdAtLec;

    @Column(nullable = false)
    private Date updatedAtLec;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Getters and Setters
    public String getLectureId() {
        return lectureId;
    }

    public void setLectureId(String lectureId) {
        this.lectureId = lectureId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public Date getCreatedAtLec() {
        return createdAtLec;
    }

    public void setCreatedAtLec(Date createdAtLec) {
        this.createdAtLec = createdAtLec;
    }

    public Date getUpdatedAtLec() {
        return updatedAtLec;
    }

    public void setUpdatedAtLec(Date updatedAtLec) {
        this.updatedAtLec = updatedAtLec;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
