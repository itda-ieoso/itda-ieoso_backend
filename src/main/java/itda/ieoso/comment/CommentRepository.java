package itda.ieoso.comment;

import itda.ieoso.Submission.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllBySubmissionOrderByCreatedAtAsc(Submission submission);

    void deleteAllBySubmission(Submission submission);
}
