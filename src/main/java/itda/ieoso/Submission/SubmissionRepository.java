package itda.ieoso.Submission;

import itda.ieoso.Course.Course;
import itda.ieoso.CourseAttendees.CourseAttendees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByAssignment_AssignmentId(Long assignmentId);
    
    @Modifying
    @Query("DELETE FROM Submission m WHERE m.assignment.assignmentId = :assignmentId")
    @Transactional
    void deleteAllByAssignmentId(@Param("assignmentId") Long assignmentId);

    List<Submission> findAllByCourseAndCourseAttendees(Course course, CourseAttendees courseAttendees);

}
