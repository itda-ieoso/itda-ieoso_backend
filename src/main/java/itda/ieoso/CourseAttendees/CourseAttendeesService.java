package itda.ieoso.CourseAttendees;

import org.springframework.stereotype.Service;

@Service
public class CourseAttendeesService {

//    private final CourseAttendeesRepository courseAttendeesRepository;
//    private final CourseRepository courseRepository;
//    private final UserRepository userRepository;
//
//    public CourseAttendeesService(CourseAttendeesRepository courseAttendeesRepository, CourseRepository courseRepository, UserRepository userRepository) {
//        this.courseAttendeesRepository = courseAttendeesRepository;
//        this.courseRepository = courseRepository;
//        this.userRepository = userRepository;
//    }
//
//    // 수업 등록 (User가 수업에 참여)
//    @Transactional
//    public CourseAttendees joinCourse(String courseId, String userId) {
//        Course course = courseRepository.findById(courseId)
//                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
//
//        CourseAttendees courseAttendees = new CourseAttendees();
//        courseAttendees.setClassId(UUID.randomUUID().toString());  // 고유 ID 생성
//        courseAttendees.setCourse(course);
//        courseAttendees.setUser(user);
//        courseAttendees.setJoinedAt(new Date());
//        courseAttendees.setClassStudentStatus(CourseAttendeesStatus.ACTIVE); // 초기 상태는 ACTIVE
//
//        return courseAttendeesRepository.save(courseAttendees);
//    }
//
//    // 수업 조회 (User가 수강한 모든 수업)
//    public List<CourseAttendees> getStudentsByUser(String userId) {
//        return courseAttendeesRepository.findByUser_UserId(userId);
//    }
//
//    // 수업 조회 (Course에 속한 모든 학생 조회)
//    public List<CourseAttendees> getStudentsByCourse(String courseId) {
//        return courseAttendeesRepository.findByCourse_CourseId(courseId);
//    }
//
//    // 수업 삭제 (사용자가 수업을 탈퇴)
//    @Transactional
//    public void dropCourse(String classId, String userId) {
//        CourseAttendees courseAttendees = courseAttendeesRepository.findByClassIdAndUser_UserId(classId, userId)
//                .orElseThrow(() -> new IllegalArgumentException("ClassStudent not found with classId: " + classId + " and userId: " + userId));
//
//        // 학생이 이 수업에서 탈퇴 상태로 변경
//        courseAttendees.setClassStudentStatus(CourseAttendeesStatus.DROPPED);
//        courseAttendeesRepository.save(courseAttendees);
//    }
}
