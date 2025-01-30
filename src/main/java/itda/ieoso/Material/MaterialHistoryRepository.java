package itda.ieoso.Material;

import itda.ieoso.Course.Course;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.Video.VideoHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MaterialHistoryRepository extends JpaRepository<MaterialHistory, Long> {
    @Modifying
    @Query("DELETE FROM MaterialHistory m WHERE m.material.materialId = :materialId")
    @Transactional
    void deleteAllByMaterialId(@Param("materialId") Long materialId);

    List<MaterialHistory> findAllByCourseAndCourseAttendees(Course course, CourseAttendees courseAttendees);


}
