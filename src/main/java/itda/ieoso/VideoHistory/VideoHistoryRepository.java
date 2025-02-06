package itda.ieoso.VideoHistory;

import itda.ieoso.Course.Course;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.Video.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VideoHistoryRepository extends JpaRepository<VideoHistory, Long> {
    @Modifying
    @Query("DELETE FROM VideoHistory m WHERE m.video.videoId = :videoId")
    @Transactional
    void deleteAllByVideoId(@Param("videoId") Long videoId);

    List<VideoHistory> findAllByCourseAndCourseAttendees(Course course, CourseAttendees courseAttendees);

    VideoHistory findByVideoAndCourseAttendees(Video video, CourseAttendees attendees);

    void deleteAllByCourse(Course course);
}
