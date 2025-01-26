package itda.ieoso.Course;

import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;
    private CourseAttendeesRepository courseAttendeesRepository;

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
    @PostMapping("/{courseId}/enter/{userId}")
    public ResponseEntity<String> enterCourse(
            @PathVariable Long courseId,
            @PathVariable Long userId,
            @RequestParam String entryCode) {
        // 서비스 메서드를 호출하여 강의실 입장 처리
        courseService.enterCourse(courseId, userId, entryCode);

        // 성공 메시지 반환
        return ResponseEntity.ok("가입되었습니다!");
    }


}

