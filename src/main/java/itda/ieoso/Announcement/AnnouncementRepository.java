package itda.ieoso.Announcement;

import itda.ieoso.Course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    Announcement findByCourseAndAnnouncementId(Course course, Long announcementId);
    List<Announcement> findAllByCourse(Course course);
}
