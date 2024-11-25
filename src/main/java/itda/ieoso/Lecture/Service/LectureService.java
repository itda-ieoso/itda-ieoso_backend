package itda.ieoso.Lecture.Service;

import itda.ieoso.Course.Domain.Course;
import itda.ieoso.Course.Repository.CourseRepository;
import itda.ieoso.Lecture.Domain.Lecture;
import itda.ieoso.Lecture.Repository.LectureRepository;
import itda.ieoso.User.Domain.User;
import itda.ieoso.User.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class LectureService {

    private final LectureRepository lectureRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public LectureService(LectureRepository lectureRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.lectureRepository = lectureRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    // 강의 생성
    @Transactional
    public Lecture createLecture(String courseId, String userId, String title, String description, String videoLink) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

        User creator = course.getCreatedBy();  // 강좌 생성자

        if (!creator.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Only the course creator can add lectures.");
        }

        Lecture lecture = new Lecture();
        lecture.setCourse(course);
        lecture.setTitle(title);
        lecture.setDescription(description);
        lecture.setCreatedAtLec(new Date());
        lecture.setUpdatedAtLec(new Date());
        lecture.setVideoLink(videoLink);

        return lectureRepository.save(lecture);
    }

    // 강의 삭제
    @Transactional
    public void deleteLecture(String lectureId, String userId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found with ID: " + lectureId));

        Course course = lecture.getCourse();
        User creator = course.getCreatedBy();  // 강좌 생성자

        if (!creator.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Only the course creator can delete lectures.");
        }

        lectureRepository.delete(lecture);
    }

    // 강의 조회 (과정 ID로 조회)
    public List<Lecture> getLecturesByCourseId(String courseId) {
        return lectureRepository.findByCourse_CourseId(courseId);
    }
}
