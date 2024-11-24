package itda.ieoso.Course.Controller;

import itda.ieoso.Course.Domain.Course;
import itda.ieoso.Course.Service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam String videoLink,
            @RequestParam(required = false) Integer maxStudents) {

        Course course = courseService.createCourse(userId, name, description, startDate, endDate, videoLink, maxStudents);
        return ResponseEntity.ok(course);
    }
}
