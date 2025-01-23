package itda.ieoso.Course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;
    private CourseRepository courseRepository;

    private final Map<Long, String> entryCodes = new HashMap<>(); // 강의실 입장 코드를 저장할 Map

    // 과정 생성
    @PostMapping("/{userId}")
    public ResponseEntity<Course> createCourse(
            @PathVariable Long userId,
            @RequestBody Course courseRequest) {
        Course course = courseService.createCourse(userId,
                courseRequest.getCourseTitle(),
                courseRequest.getCourseDescription(),
                courseRequest.getMaxStudents(),
                courseRequest.getClosedDate());
        return ResponseEntity.ok(course);
    }

    // 과정 업데이트
    @PutMapping("/{courseId}/{userId}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Long courseId,
            @PathVariable Long userId,
            @RequestBody Course courseRequest) {
        Course course = courseService.updateCourse(courseId,
                userId,
                courseRequest.getCourseTitle(),
                courseRequest.getCourseDescription(),
                courseRequest.getMaxStudents(),
                courseRequest.getClosedDate());
        return ResponseEntity.ok(course);
    }

    // 과정 삭제
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId, @RequestParam Long userId) {
        courseService.deleteCourse(courseId, userId);
        return ResponseEntity.noContent().build();
    }

    // 과정 조회
    @GetMapping("/{courseId}")
    public ResponseEntity<Course> findByCourseId(@PathVariable Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("강좌를 찾을 수 없습니다"));
        return ResponseEntity.ok(course);
    }

    // 강의실 입장 코드 생성
    @PostMapping("/{courseId}/generate-entry-code")
    public ResponseEntity<String> generateEntryCode(@PathVariable Long courseId) {
        String entryCode = UUID.randomUUID().toString().substring(0, 8); // 랜덤 코드 생성
        entryCodes.put(courseId, entryCode);
        return ResponseEntity.ok(entryCode);
    }

    // 강의실 입장
    @PostMapping("/{courseId}/enter")
    public ResponseEntity<String> enterCourse(
            @PathVariable Long courseId,
            @RequestParam String entryCode) {

        String storedCode = entryCodes.get(courseId);

        if (storedCode != null && storedCode.equals(entryCode)) {
            return ResponseEntity.ok("강의실에 성공적으로 입장하였습니다!");
        } else {
            return ResponseEntity.status(403).body("입장 코드가 잘못되었습니다.");
        }
    }
}

