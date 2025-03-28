package itda.ieoso.MaterialHistory;

import itda.ieoso.Course.Course;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.Material.Material;
import itda.ieoso.VideoHistory.VideoHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MaterialHistoryRepository extends JpaRepository<MaterialHistory, Long> {
    void deleteAllByMaterial(Material material);

    List<MaterialHistory> findAllByCourseAndCourseAttendees(Course course, CourseAttendees courseAttendees);

    MaterialHistory findByMaterialAndCourseAttendees(Material material, CourseAttendees attendees);

    void deleteAllByCourse(Course course);

    void deleteAllByCourseAttendees(CourseAttendees attendees);

    Optional<MaterialHistory> findByMaterial_MaterialIdAndCourseAttendees_CourseAttendeesId(Long materialId, Long courseAttendeesId);
}
