package itda.ieoso.Video;

import itda.ieoso.Course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Video findByCourseAndVideoId(Course course, Long videoId);
    List<Video> findAllByCourse(Course course);
}
