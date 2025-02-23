package itda.ieoso.Material;

import itda.ieoso.Course.Course;
import itda.ieoso.Video.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    Material findByCourseAndMaterialId(Course course, Long material);
    List<Material> findAllByCourse(Course course);
    List<Material> findByCourseAndStartDateBeforeAndEndDateAfter(Course course, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT m FROM Material m WHERE m.course = :course " +
            "AND CAST(m.startDate as localdate ) <= :date " +
            "AND cast(m.endDate as localdate ) >= :date")
    List<Material> findByCourseAndDateRange(@Param("course") Course course,
                                            @Param("date") LocalDate date);
}
