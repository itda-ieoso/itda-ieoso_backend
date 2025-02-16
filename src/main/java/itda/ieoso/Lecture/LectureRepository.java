package itda.ieoso.Lecture;

import itda.ieoso.Course.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    List<Lecture> findAllByCourse(Course course);
    Optional<Lecture> findByCourse_CourseIdAndLectureId(Long courseId, Long lectureId);
    List<Lecture> findByLectureIdIn(List<Long> lectureIds);
    List<Lecture> findAllByCourse_CourseId(Long courseId);
}
