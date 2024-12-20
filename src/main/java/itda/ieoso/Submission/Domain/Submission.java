package itda.ieoso.Submission.Domain;

import itda.ieoso.Assignment.Domain.Assignment;
import itda.ieoso.User.Domain.User;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Submission {

    @Id
    @Column(name = "submission_id", nullable = false)
    private String submissionId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date submittedAt;

    @Column(length = 1000)
    private String textContent;

    @Column(length = 255)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus subStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getters and Setters
    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    public Date getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Date submittedAt) {
        this.submittedAt = submittedAt;
    }
}