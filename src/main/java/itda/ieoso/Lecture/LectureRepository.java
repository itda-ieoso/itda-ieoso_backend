package itda.ieoso.Lecture;

import itda.ieoso.Course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    List<Lecture> findAllByCourse_CourseId(Long courseId);
    List<Lecture> findAllByCourse(Course course);
}
