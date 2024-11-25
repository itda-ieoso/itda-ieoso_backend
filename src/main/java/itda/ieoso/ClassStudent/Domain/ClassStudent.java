package itda.ieoso.ClassStudent.Domain;

import itda.ieoso.Course.Domain.Course;
import itda.ieoso.User.Domain.User;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class ClassStudent {

    @Id
    @Column(name = "class_id", nullable = false)
    private String classId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date joinedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "class_student_status", nullable = false)
    private ClassStudentStatus classStudentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getters and Setters
    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
    }

    public ClassStudentStatus getClassStatus() {
        return classStudentStatus;
    }

    public void setClassStudentStatus(ClassStudentStatus classStudentStatus) {
        this.classStudentStatus = classStudentStatus;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
