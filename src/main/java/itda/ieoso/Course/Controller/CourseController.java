package itda.ieoso.Course.Controller;

import itda.ieoso.Course.Domain.Course;
import itda.ieoso.Course.Service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // 과정 생성
    @PostMapping
    public ResponseEntity<Course> createCourse(
            @RequestParam String userId,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam Date startDate,
            @RequestParam Date endDate,
            @RequestParam(required = false) Integer maxStudents) {

        Course course = courseService.createCourse(userId, name, description, startDate, endDate, maxStudents);
        return ResponseEntity.ok(course);
    }
    // 과정 업데이트
    @PutMapping("/{courseId}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable String courseId,
            @RequestParam String userId,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam Date startDate,
            @RequestParam Date endDate,
            @RequestParam(required = false) Integer maxStudents) {

        Course course = courseService.updateCourse(courseId, userId, name, description, startDate, endDate, maxStudents);
        return ResponseEntity.ok(course);
    }

    // 과정 삭제
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable String courseId,
            @RequestParam String userId) {

        courseService.deleteCourse(courseId, userId);
        return ResponseEntity.noContent().build();
    }
    // 과정 조회
    @GetMapping("/{courseId}")
    public ResponseEntity<Course> findByCourseId(@PathVariable String courseId) {
        Course course = courseService.findByCourseId(courseId);
        return ResponseEntity.ok(course);
    }
}
