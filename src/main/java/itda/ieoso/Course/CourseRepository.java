package itda.ieoso.Course;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, String> {
//    Optional<Course> findByCourseId(String courseId);
//    boolean existsByCourseIdAndCreatedBy_UserId(String courseId, String userId);
}
