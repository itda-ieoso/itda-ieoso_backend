package itda.ieoso.Assignment;

import itda.ieoso.Course.Course;
import itda.ieoso.Submission.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    Assignment findByCourseAndAssignmentId(Course course, Long assignmentId);
    List<Assignment> findByLecture_LectureIdIn(@Param("lectureIds") List<Long> lectureIds);

    List<Assignment> findAllByCourse(Course course);
}
