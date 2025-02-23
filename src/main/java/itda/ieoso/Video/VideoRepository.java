package itda.ieoso.Video;

import itda.ieoso.Course.Course;
import itda.ieoso.Lecture.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Video findByCourseAndVideoId(Course course, Long videoId);
    List<Video> findAllByCourse(Course course);
    List<Video> findByCourseAndStartDateBeforeAndEndDateAfter(Course course, LocalDateTime start, LocalDateTime end);

    @Query("SELECT v FROM Video v WHERE v.course = :course " +
            "AND CAST(v.startDate as localdate) <= :date " +
            "AND cast(v.endDate as localdate ) >= :date")
    List<Video> findByCourseAndDateRange(@Param("course") Course course,
                                         @Param("date") LocalDate date);
}
