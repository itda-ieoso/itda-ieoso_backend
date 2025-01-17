package itda.ieoso.Course;

import org.springframework.stereotype.Service;

@Service
public class CourseService {

//    @Autowired
//    private CourseRepository courseRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    // 과정 생성
//    public Course createCourse(String userId, String name, String description, Date startDate, Date endDate, Integer maxStudents) {
//        Optional<User> userOpt = userRepository.findById(userId);
//        if (!userOpt.isPresent()) {
//            throw new IllegalArgumentException("올바르지 않은 user_id");
//        }
//
//        User user = userOpt.get();
//        Course course = new Course();
//        course.setCourseId(UUID.randomUUID().toString()); // UUID 생성
//        course.setName(name);
//        course.setDescription(description);
//        course.setStartDate(startDate);
//        course.setEndDate(endDate);
//        course.setMaxStudents(maxStudents);
//        course.setCreatedBy(user);
//
//        // 저장 후 반환
//        return courseRepository.save(course);
//    }
//    // 과정 업데이트
//    @Transactional
//    public Course updateCourse(String courseId, String userId, String name, String description, Date startDate, Date endDate, Integer maxStudents) {
//        Course course = courseRepository.findById(courseId)
//                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));
//
//        if (!course.getCreatedBy().getUserId().equals(userId)) {
//            throw new IllegalArgumentException("Only the course creator can update the course.");
//        }
//
//        course.setName(name);
//        course.setDescription(description);
//        course.setStartDate(startDate);
//        course.setEndDate(endDate);
//        course.setMaxStudents(maxStudents);
//        course.setUpdatedAt(new Date());
//
//        return courseRepository.save(course);
//    }
//
//    // 과정 삭제
//    @Transactional
//    public void deleteCourse(String courseId, String userId) {
//        Course course = courseRepository.findById(courseId)
//                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));
//
//        if (!course.getCreatedBy().getUserId().equals(userId)) {
//            throw new IllegalArgumentException("Only the course creator can delete the course.");
//        }
//
//        courseRepository.delete(course);
//    }
//
//    // 과정 조회
//    public Course findByCourseId(String courseId) {
//        return courseRepository.findById(courseId)
//                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));
//    }
}

