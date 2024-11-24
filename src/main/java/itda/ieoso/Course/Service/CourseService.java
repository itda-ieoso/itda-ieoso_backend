package itda.ieoso.Course.Service;

import itda.ieoso.Course.Domain.Course;
import itda.ieoso.Course.Repository.CourseRepository;
import itda.ieoso.User.Domain.User;
import itda.ieoso.User.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    // 과정 생성
    public Course createCourse(String userId, String name, String description, Date startDate, Date endDate, String videoLink, Integer maxStudents) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("올바르지 않은 user_id");
        }

        User user = userOpt.get();
        Course course = new Course();
        course.setCourseId(UUID.randomUUID().toString()); // UUID 생성
        course.setName(name);
        course.setDescription(description);
        course.setStartDate(startDate);
        course.setEndDate(endDate);
        course.setVideoLink(videoLink);
        course.setMaxStudents(maxStudents);
        course.setCreatedBy(user);

        // 저장 후 반환
        return courseRepository.save(course);
    }
}

