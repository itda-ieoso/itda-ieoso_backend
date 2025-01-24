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

    // 강의실 생성 및 입장 코드 생성
    @PostMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> createCourse(@PathVariable Long userId, @RequestBody Course courseRequest) {
        // courseService에서 강좌 생성 처리
        CourseDTO courseDTO = courseService.createCourse(userId, courseRequest.getCourseTitle(),
                courseRequest.getCourseDescription(), courseRequest.getMaxStudents(), courseRequest.getClosedDate());
        System.out.println(courseRequest);
        // 입장 코드 가져오기
        String entryCode = courseDTO.getEntryCode();

        // 반환할 Map 설정
        Map<String, Object> response = new HashMap<>();
        response.put("course", courseDTO);
        response.put("entryCode", entryCode);

        return ResponseEntity.ok(response); // OK 상태로 반환
    }

    // 강의실 수정
    @PutMapping("/{courseId}/{userId}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long courseId, @PathVariable Long userId, @RequestBody Course courseRequest) {
        // 강좌 수정 처리
        CourseDTO updatedCourseDTO = courseService.updateCourse(courseId, userId, courseRequest.getCourseTitle(),
                courseRequest.getCourseDescription(), courseRequest.getMaxStudents(), courseRequest.getClosedDate(), courseRequest.getCourseThumbnail());

        return ResponseEntity.ok(updatedCourseDTO); // 수정된 강좌 반환
    }

    // 강의실 삭제
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId, @RequestParam Long userId) {
        courseService.deleteCourse(courseId, userId);
        return ResponseEntity.noContent().build();
    }

    // 강의실 조회
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDTO> findByCourseId(@PathVariable Long courseId) {
        CourseDTO courseDTO = courseService.getCourseById(courseId);
        return ResponseEntity.ok(courseDTO);
    }

    // 강의실 입장
    @PostMapping("/{courseId}/enter")
    public ResponseEntity<String> enterCourse(
            @PathVariable Long courseId,
            @RequestParam String entryCode) {

        // Service를 통해 입장 코드 검증
        boolean isValid = courseService.validateEntryCode(courseId, entryCode);

        if (isValid) {
            return ResponseEntity.ok("강의실에 성공적으로 입장하였습니다!");
        } else {
            return ResponseEntity.status(403).body("입장 코드가 잘못되었습니다.");
        }
    }

}

