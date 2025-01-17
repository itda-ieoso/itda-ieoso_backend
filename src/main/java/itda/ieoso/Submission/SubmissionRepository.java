package itda.ieoso.Submission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, String> {

    // 특정 과제에 대한 제출물을 조회
//    List<Submission> findByAssignment_AssignmentId(String assignmentId);
//
//    // 특정 학습자가 제출한 과제 조회
//    List<Submission> findByUser_UserId(String userId);
}
