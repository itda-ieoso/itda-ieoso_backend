package itda.ieoso.ContentOrder;

import itda.ieoso.Course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContentOrderRepository extends JpaRepository<ContentOrder, Long> {
    List<ContentOrder> findByCourseOrderByOrderIndexAsc(Course course);
    void deleteByContentIdAndContentType(Long contentId, String contentType);

    @Query("SELECT co FROM ContentOrder co WHERE co.course.courseId = :courseId ORDER BY co.orderIndex ASC")
    List<ContentOrder> findOrderedByCourseId(@Param("courseId") Long courseId);

}
