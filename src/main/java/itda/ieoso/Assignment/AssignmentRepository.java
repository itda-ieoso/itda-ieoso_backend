package itda.ieoso.Assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, String> {

    // 특정 강의에 대한 과제 조회
//    List<Assignment> findByCourse_CourseIdAndLecture_LectureId(String courseId, String lectureId);
}
