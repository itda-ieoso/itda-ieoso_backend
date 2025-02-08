package itda.ieoso.Course;

import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.Response.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;
    private CourseAttendeesRepository courseAttendeesRepository;

    // 강의실 생성 및 입장 코드 생성
    @PostMapping("/{userId}")
    public DataResponse<CourseDTO> createCourse(@PathVariable Long userId) {
        // courseService에서 강좌 생성 처리
        DataResponse<CourseDTO> response = new DataResponse<>(courseService.createCourse(userId));
        return response;
    }

    // 강의실 설정창 업데이트
    @PutMapping("/{courseId}/{userId}/setting")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long courseId,
                                                  @PathVariable Long userId,
                                                  @RequestBody CourseDTO.BasicUpdateRequest request) {
        CourseDTO updatedCourseDTO = courseService.updateCourse(courseId, userId, request);

        return ResponseEntity.ok(updatedCourseDTO);
    }

    // 강의실 개요 업데이트
    @PutMapping("/{courseId}/{userId}/overview")
    public ResponseEntity<?> updateCourseOverview(@PathVariable Long courseId,
                                                  @PathVariable Long userId ,
                                                  @RequestBody CourseDTO.OverviewUpdateRequest request ) {
        CourseDTO updateCourseDto = courseService.updateCourseOverview(courseId,userId, request);
        return ResponseEntity.ok(updateCourseDto);
    }

    // 강의실 삭제
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId, @RequestParam Long userId) {
        courseService.deleteCourse(courseId, userId);
        return ResponseEntity.noContent().build();
    }

    // 강의실 설정 정보 조회(설정 & 개요 페이지)
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

    // 사용자가 가입한 강의실 목록 조회
    @GetMapping("/{userId}/my-courses")
    public ResponseEntity<List<CourseDTO>> getCoursesByUser(@PathVariable Long userId) {
        List<CourseDTO> courses = courseService.getCoursesByUser(userId);
        return ResponseEntity.ok(courses);
    }

}

