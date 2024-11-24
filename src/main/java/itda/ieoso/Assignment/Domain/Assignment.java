package itda.ieoso.Assignment.Domain;

import itda.ieoso.Course.Domain.Course;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Assignment {

    @Id
    @Column(name = "assignment_id", nullable = false)
    private String assignmentId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dueDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date hwCreatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date hwUpdatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Getters and Setters
    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
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

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getHwCreatedAt() {
        return hwCreatedAt;
    }

    public void setHwCreatedAt(Date hwCreatedAt) {
        this.hwCreatedAt = hwCreatedAt;
    }

    public Date getHwUpdatedAt() {
        return hwUpdatedAt;
    }

    public void setHwUpdatedAt(Date hwUpdatedAt) {
        this.hwUpdatedAt = hwUpdatedAt;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
