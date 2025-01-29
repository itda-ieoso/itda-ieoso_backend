package itda.ieoso.Submission;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.Course.Course;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.User.User;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id", nullable = false)
    private Long submissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_attendees_id", nullable = false)
    private CourseAttendees courseAttendees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 1000)
    private String textContent;

    @ElementCollection
    @CollectionTable(name = "submission_file_urls", joinColumns = @JoinColumn(name = "submission_id"))
    @Column(name = "file_url")
    private List<String> fileUrls;

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
    public Submission(Assignment assignment, User user, String textContent, List<String> fileUrls,
                      SubmissionStatus submissionStatus, LocalDateTime submittedAt,
                      boolean gradeStatus, int score) {
        this.assignment = assignment;
        this.user = user;
        this.textContent = textContent;
        this.fileUrls = fileUrls;
        this.submissionStatus = submissionStatus;
        this.submittedAt = submittedAt;
        this.gradeStatus = gradeStatus;
        this.score = score;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public void setFileUrls(List<String> fileUrls) {
        this.fileUrls = fileUrls;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public void setSubmissionStatus(SubmissionStatus submissionStatus) {
        this.submissionStatus = submissionStatus;
    }
}