package itda.ieoso.ClassStudent.Repository;

import itda.ieoso.ClassStudent.Domain.ClassStudent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassStudentRepository extends JpaRepository<ClassStudent, String> {
    Optional<ClassStudent> findByClassIdAndUser_UserId(String classId, String userId);
    boolean existsByCourse_CourseIdAndUser_UserId(String courseId, String userId);
    List<ClassStudent> findByUser_UserId(String userId);
    List<ClassStudent> findByCourse_CourseId(String courseId);
}
