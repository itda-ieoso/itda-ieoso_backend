package itda.ieoso.CourseAttendees;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseAttendeesRepository extends JpaRepository<CourseAttendees, Long> {
    boolean existsByCourse_CourseIdAndUser_UserId(Long courseId, Long userId);
}
