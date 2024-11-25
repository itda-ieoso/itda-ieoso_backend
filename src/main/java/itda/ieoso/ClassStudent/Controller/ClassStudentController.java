package itda.ieoso.ClassStudent.Controller;

import itda.ieoso.ClassStudent.Domain.ClassStudent;
import itda.ieoso.ClassStudent.Service.ClassStudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/class")
public class ClassStudentController {

    private final ClassStudentService classStudentService;

    public ClassStudentController(ClassStudentService classStudentService) {
        this.classStudentService = classStudentService;
    }

    // 수업 등록 (User가 수업에 참여)
    @PostMapping("/join")
    public ResponseEntity<ClassStudent> joinCourse(@RequestParam String courseId, @RequestParam String userId) {
        ClassStudent classStudent = classStudentService.joinCourse(courseId, userId);
        return ResponseEntity.ok(classStudent);
    }

    // 수업 조회 (User가 수강한 모든 수업)
    @GetMapping("/students/{userId}")
    public ResponseEntity<List<ClassStudent>> getCoursesByUser(@PathVariable String userId) {
        List<ClassStudent> classStudents = classStudentService.getStudentsByUser(userId);
        return ResponseEntity.ok(classStudents);
    }

    // 수업 조회 (Course에 속한 모든 학생 조회)
    @GetMapping("/students/course/{courseId}")
    public ResponseEntity<List<ClassStudent>> getStudentsByCourse(@PathVariable String courseId) {
        List<ClassStudent> classStudents = classStudentService.getStudentsByCourse(courseId);
        return ResponseEntity.ok(classStudents);
    }

    // 수업 탈퇴 (사용자가 수업을 탈퇴)
    @PostMapping("/drop")
    public ResponseEntity<Void> dropCourse(@RequestParam String classId, @RequestParam String userId) {
        classStudentService.dropCourse(classId, userId);
        return ResponseEntity.noContent().build();
    }
}
