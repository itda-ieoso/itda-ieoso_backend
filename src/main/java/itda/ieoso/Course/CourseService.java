package itda.ieoso.Course;

import itda.ieoso.User.User;
import itda.ieoso.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    // 강좌 생성
    public Course createCourse(Long userId, String courseTitle, String courseDescription,
                               int maxStudents, LocalDate closedDate) {

        // userId로 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // Course 객체를 생성 (생성자 사용)
        Course course = new Course(user, courseTitle, courseDescription, maxStudents, closedDate);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now()); // 처음 생성 시 updatedAt도 현재 시간

        // 데이터베이스에 저장
        return courseRepository.save(course);
    }

    // 강좌 수정
    public Course updateCourse(Long courseId, Long userId, String courseTitle,
                               String courseDescription, int maxStudents, LocalDate closedDate) {

        // 기존 강좌 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("강좌를 찾을 수 없습니다"));

        // 강좌를 생성한 사용자 ID와 요청한 사용자 ID가 일치하는지 확인
        if (!course.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("이 강좌를 수정할 권한이 없습니다.");
        }

        // 새로운 강좌 객체 생성 (기존 객체를 수정하지 않고 새로운 객체로 교체)
        Course updatedCourse = new Course(course.getUser(), courseTitle, courseDescription,
                maxStudents, closedDate);

        updatedCourse.setCreatedAt(course.getCreatedAt().toLocalDateTime());  // 기존 createdAt 유지
        updatedCourse.setUpdatedAt(LocalDateTime.now());   // updatedAt 갱신

        // 데이터베이스에 저장
        return courseRepository.save(updatedCourse);
    }

    // 강좌 삭제
    public void deleteCourse(Long courseId, Long userId) {

        // 강좌 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("강좌를 찾을 수 없습니다"));

        // 강좌를 생성한 사용자 ID와 요청한 사용자 ID가 일치하는지 확인
        if (!course.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("이 강좌를 삭제할 권한이 없습니다.");
        }

        // 강좌 삭제
        courseRepository.delete(course);
    }

    // 강좌 조회
    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("강좌를 찾을 수 없습니다"));
    }
}




