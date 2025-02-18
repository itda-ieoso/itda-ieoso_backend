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
    void deleteAllByVideo(Video video);

    List<VideoHistory> findAllByCourseAndCourseAttendees(Course course, CourseAttendees courseAttendees);

    VideoHistory findByVideoAndCourseAttendees(Video video, CourseAttendees attendees);

    void deleteAllByCourse(Course course);

    @Query("SELECT vh FROM VideoHistory vh WHERE vh.video.videoId IN :videoIds AND vh.courseAttendees = :courseAttendees")
    List<VideoHistory> findByVideo_VideoIdInAndCourseAttendeesIn(@Param("videoIds") List<Long> videoIds, @Param("courseAttendees") CourseAttendees courseAttendees);

    //List<VideoHistory> findByVideo_StartDateAndVideo_EndDate(Date startDate, Date endDate);
}
