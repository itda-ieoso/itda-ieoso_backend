package itda.ieoso.Video;

import itda.ieoso.Course.Course;
import itda.ieoso.Lecture.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Video findByCourseAndVideoId(Course course, Long videoId);
    List<Video> findAllByCourse(Course course);
    List<Video> findByCourseAndStartDateBeforeAndEndDateAfter(Course course, LocalDateTime start, LocalDateTime end);
}
