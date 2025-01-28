package itda.ieoso.Submission;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.User.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id", nullable = false)
    private Long submissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 1000)
    private String textContent;

    @Column(length = 255)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus submissionStatus;

    @Column
    private LocalDateTime submittedAt;

    @Column
    private boolean gradeStatus;

    @Column
    private int score = 0;

    @Builder
    public Submission(Assignment assignment, User user, String textContent, String fileUrl,
                      SubmissionStatus submissionStatus, LocalDateTime submittedAt,
                      boolean gradeStatus, int score) {
        this.assignment = assignment;
        this.user = user;
        this.textContent = textContent;
        this.fileUrl = fileUrl;
        this.submissionStatus = submissionStatus;
        this.submittedAt = submittedAt;
        this.gradeStatus = gradeStatus;
        this.score = score;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public void setSubmissionStatus(SubmissionStatus submissionStatus) {
        this.submissionStatus = submissionStatus;
    }
}