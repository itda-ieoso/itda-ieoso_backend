package itda.ieoso.MaterialHistory;

import itda.ieoso.Course.Course;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.Material.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MaterialHistoryRepository extends JpaRepository<MaterialHistory, Long> {
    void deleteAllByMaterial(Material material);

    List<MaterialHistory> findAllByCourseAndCourseAttendees(Course course, CourseAttendees courseAttendees);

    MaterialHistory findByMaterialAndCourseAttendees(Material material, CourseAttendees attendees);

    void deleteAllByCourse(Course course);
}
