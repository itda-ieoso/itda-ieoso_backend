package itda.ieoso.CourseAttendees;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseAttendeesRepository extends JpaRepository<CourseAttendees, String> {
    Optional<CourseAttendees> findByClassIdAndUser_UserId(String classId, String userId);
    boolean existsByCourse_CourseIdAndUser_UserId(String courseId, String userId);
    List<CourseAttendees> findByUser_UserId(String userId);
    List<CourseAttendees> findByCourse_CourseId(String courseId);
}
