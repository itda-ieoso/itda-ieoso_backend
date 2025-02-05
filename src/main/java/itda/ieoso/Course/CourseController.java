package itda.ieoso.Course;

import itda.ieoso.Course.Dto.CourseOverviewUpdateDto;
import itda.ieoso.Course.Dto.CourseUpdateDto;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.Lecture.CurriculumModificationRequest;
import itda.ieoso.Response.DataResponse;
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
    public DataResponse<CourseDTO> createCourse(@PathVariable Long userId) {
        // courseService에서 강좌 생성 처리
        DataResponse<CourseDTO> response = new DataResponse<>(courseService.createCourse(userId));
        return response;
    }

    // 강의실 설정창 업데이트
    @PutMapping("/{courseId}/{userId}/setting")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long courseId,
                                                  @PathVariable Long userId,
                                                  @RequestBody CourseUpdateDto courseRequest) {
        // 강좌 수정 처리
        CourseDTO updatedCourseDTO = courseService.updateCourse(courseId, userId, courseRequest);

        return ResponseEntity.ok(updatedCourseDTO); // 수정된 강좌 반환
    }

    // 강의실 개요 업데이트
    @PutMapping("/{courseId}/{userId}/overview")
    public ResponseEntity<?> updateCourseOverview(@PathVariable Long courseId,
                                                  @PathVariable Long userId ,
                                                  @RequestBody CourseOverviewUpdateDto courseRequest ) {
        // 강좌 수정
        CourseDTO updateCourseDto = courseService.updateCourseOverview(courseId,userId, courseRequest);
        return ResponseEntity.ok(updateCourseDto);
    }

    // 강의실 삭제
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId, @RequestParam Long userId) {
        courseService.deleteCourse(courseId, userId);
        return ResponseEntity.noContent().build();
    }

    // 강의실 조회(설정 & 개요 페이지)
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

