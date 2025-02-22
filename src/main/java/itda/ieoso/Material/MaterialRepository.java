package itda.ieoso.Material;

import itda.ieoso.Course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    Material findByCourseAndMaterialId(Course course, Long material);
    List<Material> findAllByCourse(Course course);
    List<Material> findByCourseAndStartDateBeforeAndEndDateAfter(Course course, LocalDateTime startDate, LocalDateTime endDate);
}
