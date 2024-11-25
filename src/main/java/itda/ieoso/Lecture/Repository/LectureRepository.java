package itda.ieoso.Lecture.Repository;

import itda.ieoso.Lecture.Domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LectureRepository extends JpaRepository<Lecture, String> {
    List<Lecture> findByCourse_CourseId(String courseId);  // 강좌 ID로 강의를 조회
    Optional<Lecture> findByLectureId(String lectureId);    // 강의 ID로 강의를 조회
}
