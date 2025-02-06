package itda.ieoso.CourseAttendees;

import itda.ieoso.Course.Course;
import itda.ieoso.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseAttendeesRepository extends JpaRepository<CourseAttendees, Long> {
    boolean existsByCourseAndUser(Course course, User user);
    boolean existsByCourse_CourseIdAndUser_UserId(Long courseId, Long userId);
    Optional<CourseAttendees> findByCourseAndUser(Course course, User user);
    List<CourseAttendees> findAllByCourse(Course course);
    List<CourseAttendees> findByCourse_CourseId(Long courseId);

    void deleteAllByCourse(Course course);

    List<CourseAttendees> findByUser_UserId(Long userId);
}
