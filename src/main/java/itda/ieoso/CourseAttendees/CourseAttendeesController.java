package itda.ieoso.CourseAttendees;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/class")
public class CourseAttendeesController {

//    private final CourseAttendeesService courseAttendeesService;
//
//    public CourseAttendeesController(CourseAttendeesService courseAttendeesService) {
//        this.courseAttendeesService = courseAttendeesService;
//    }
//
//    // 수업 등록 (User가 수업에 참여)
//    @PostMapping("/join")
//    public ResponseEntity<CourseAttendees> joinCourse(@RequestParam String courseId, @RequestParam String userId) {
//        CourseAttendees courseAttendees = courseAttendeesService.joinCourse(courseId, userId);
//        return ResponseEntity.ok(courseAttendees);
//    }
//
//    // 수업 조회 (User가 수강한 모든 수업)
//    @GetMapping("/students/{userId}")
//    public ResponseEntity<List<CourseAttendees>> getCoursesByUser(@PathVariable String userId) {
//        List<CourseAttendees> courseAttendees = courseAttendeesService.getStudentsByUser(userId);
//        return ResponseEntity.ok(courseAttendees);
//    }
//
//    // 수업 조회 (Course에 속한 모든 학생 조회)
//    @GetMapping("/students/course/{courseId}")
//    public ResponseEntity<List<CourseAttendees>> getStudentsByCourse(@PathVariable String courseId) {
//        List<CourseAttendees> courseAttendees = courseAttendeesService.getStudentsByCourse(courseId);
//        return ResponseEntity.ok(courseAttendees);
//    }
//
//    // 수업 탈퇴 (사용자가 수업을 탈퇴)
//    @PostMapping("/drop")
//    public ResponseEntity<Void> dropCourse(@RequestParam String classId, @RequestParam String userId) {
//        courseAttendeesService.dropCourse(classId, userId);
//        return ResponseEntity.noContent().build();
//    }
}
