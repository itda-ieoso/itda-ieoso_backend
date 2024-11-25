package itda.ieoso.ClassStudent.Service;

import itda.ieoso.ClassStudent.Domain.ClassStudent;
import itda.ieoso.ClassStudent.Domain.ClassStudentStatus;
import itda.ieoso.ClassStudent.Repository.ClassStudentRepository;
import itda.ieoso.Course.Domain.Course;
import itda.ieoso.Course.Repository.CourseRepository;
import itda.ieoso.User.Domain.User;
import itda.ieoso.User.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ClassStudentService {

    private final ClassStudentRepository classStudentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public ClassStudentService(ClassStudentRepository classStudentRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.classStudentRepository = classStudentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    // 수업 등록 (User가 수업에 참여)
    @Transactional
    public ClassStudent joinCourse(String courseId, String userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        ClassStudent classStudent = new ClassStudent();
        classStudent.setClassId(UUID.randomUUID().toString());  // 고유 ID 생성
        classStudent.setCourse(course);
        classStudent.setUser(user);
        classStudent.setJoinedAt(new Date());
        classStudent.setClassStudentStatus(ClassStudentStatus.ACTIVE); // 초기 상태는 ACTIVE

        return classStudentRepository.save(classStudent);
    }

    // 수업 조회 (User가 수강한 모든 수업)
    public List<ClassStudent> getStudentsByUser(String userId) {
        return classStudentRepository.findByUser_UserId(userId);
    }

    // 수업 조회 (Course에 속한 모든 학생 조회)
    public List<ClassStudent> getStudentsByCourse(String courseId) {
        return classStudentRepository.findByCourse_CourseId(courseId);
    }

    // 수업 삭제 (사용자가 수업을 탈퇴)
    @Transactional
    public void dropCourse(String classId, String userId) {
        ClassStudent classStudent = classStudentRepository.findByClassIdAndUser_UserId(classId, userId)
                .orElseThrow(() -> new IllegalArgumentException("ClassStudent not found with classId: " + classId + " and userId: " + userId));

        // 학생이 이 수업에서 탈퇴 상태로 변경
        classStudent.setClassStudentStatus(ClassStudentStatus.DROPPED);
        classStudentRepository.save(classStudent);
    }
}
