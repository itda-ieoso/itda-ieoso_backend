package itda.ieoso.CourseAttendees;

import itda.ieoso.Course.Course;
import itda.ieoso.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseAttendeesRepository extends JpaRepository<CourseAttendees, Long> {
    boolean existsByCourseAndUser(Course course, User user);
}
