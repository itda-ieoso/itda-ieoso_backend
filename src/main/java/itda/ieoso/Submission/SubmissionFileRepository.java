package itda.ieoso.Submission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionFileRepository extends JpaRepository<SubmissionFile, Long> {

    void deleteBySubmissionFileUrlIn(List<String> deleteFileUrls);
}
