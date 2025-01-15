package itda.ieoso.Submission;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.User.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.sql.Timestamp;

@Entity
@Getter
public class Submission {

    @Id
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

    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp submittedAt;

    @Column
    private boolean gradeStatus;

    @Column
    private int score;

}