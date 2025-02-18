package itda.ieoso.Submission;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.Course.Course;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.VideoHistory.VideoHistory;
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
    
    void deleteAllByAssignment(Assignment assignment);

    List<Submission> findAllByCourseAndCourseAttendees(Course course, CourseAttendees courseAttendees);

    Submission findByAssignmentAndCourseAttendees(Assignment assignment, CourseAttendees courseAttendees);

    void deleteAllByCourse(Course course);

    @Query("SELECT sm FROM Submission sm WHERE sm.assignment.assignmentId IN :assignmentIds AND sm.courseAttendees = :courseAttendees")
    List<Submission> findByAssignment_AssignmentIdInAndCourseAttendeesIn(@Param("assignmentIds") List<Long> assignmentIds, @Param("courseAttendees") CourseAttendees courseAttendees);

}
