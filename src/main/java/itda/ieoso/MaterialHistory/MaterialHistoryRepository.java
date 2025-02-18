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

public interface MaterialHistoryRepository extends JpaRepository<MaterialHistory, Long> {
    void deleteAllByMaterial(Material material);

    List<MaterialHistory> findAllByCourseAndCourseAttendees(Course course, CourseAttendees courseAttendees);

    MaterialHistory findByMaterialAndCourseAttendees(Material material, CourseAttendees attendees);

    void deleteAllByCourse(Course course);

    @Query("SELECT mh FROM MaterialHistory mh WHERE mh.material.materialId IN :materialIds AND mh.courseAttendees = :courseAttendees")
    List<MaterialHistory> findByMaterial_MaterialIdInAndCourseAttendeesIn(@Param("materialIds") List<Long> materialIds, @Param("courseAttendees") CourseAttendees courseAttendees);

}
