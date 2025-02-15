package itda.ieoso.Announcement;

import itda.ieoso.Course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    Announcement findByCourseAndAnnouncementId(Course course, Long announcementId);
    Announcement findByCourse_CourseIdAndAnnouncementId(Long courseId, Long announcementId);
    List<Announcement> findAllByCourse_CourseId(Long courseId);

    void deleteAllByCourse(Course course);
}
