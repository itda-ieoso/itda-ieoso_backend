package itda.ieoso.Assignment;

import itda.ieoso.Course.Course;
import itda.ieoso.Submission.Submission;
import itda.ieoso.Video.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    Assignment findByCourseAndAssignmentId(Course course, Long assignmentId);
    List<Assignment> findByLecture_LectureIdIn(@Param("lectureIds") List<Long> lectureIds);
    List<Assignment> findAllByCourse(Course course);

    Optional<Assignment> findByAssignmentId(Long assignmentId);
    List<Assignment> findByCourseAndStartDateBeforeAndEndDateAfter(Course course, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT a FROM Assignment a WHERE a.course = :course " +
            "AND CAST(a.startDate as localdate) <= :date " +
            "AND cast(a.endDate as localdate ) >= :date")
    List<Assignment> findByCourseAndDateRange(@Param("course") Course course,
                                              @Param("date") LocalDate date);
}
