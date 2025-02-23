package itda.ieoso.ContentOrder;

import itda.ieoso.Course.Course;
import itda.ieoso.Lecture.Lecture;
import jdk.jfr.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContentOrderRepository extends JpaRepository<ContentOrder, Long> {
    List<ContentOrder> findByCourse_CourseIdAndLecture_LectureIdOrderByOrderIndexAsc(Long course, Long lecture);
    void deleteByContentIdAndContentType(Long contentId, String contentType);
    void deleteAllByCourse(Course course);
    void deleteAllByLecture(Lecture lecture);
    @Query("SELECT co FROM ContentOrder co WHERE co.course.courseId = :courseId AND co.lecture.lectureId = :lectureId ORDER BY co.orderIndex ASC")
    List<ContentOrder> findOrderedByCourseIdAndLectureId(@Param("courseId") Long courseId, @Param("lectureId") Long lectureId);

    List<ContentOrder> findByContentTypeAndContentIdIn(String contentType, List<Long> contentId);

    ContentOrder findByContentTypeAndContentId(String contentType, Long contentId);
}
