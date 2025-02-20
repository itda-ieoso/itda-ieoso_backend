package itda.ieoso.Course;

import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.Response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // 강의실 생성 및 입장 코드 생성
    @PostMapping("/{userId}")
    public Response<CourseDTO> createCourse(@PathVariable Long userId, @RequestHeader("Authorization") String token) {

        // courseService에서 강좌 생성 처리
        return Response.success("빈 강의실 생성 및 입장코드 생성", courseService.createCourse(token));
    }

    // 강의실 설정창 업데이트
    @PutMapping("/{courseId}/{userId}/setting")
    public Response<CourseDTO> updateCourse(@PathVariable Long courseId,
                                                  @PathVariable Long userId,
                                                  @RequestBody CourseDTO.BasicUpdateRequest request,
                                                  @RequestHeader("Authorization") String token) {
        CourseDTO updatedCourseDTO = courseService.updateCourse(courseId, token, request);


        return Response.success("강의실 설정창 업데이트", updatedCourseDTO); // 수정된 강좌 반환
    }

    // 강의실 개요 업데이트
    @PutMapping("/{courseId}/{userId}/overview")
    public Response<CourseDTO> updateCourseOverview(@PathVariable Long courseId,
                                                    @PathVariable Long userId,
                                                    @RequestParam(value = "description", required = false) String description,
                                                    @RequestParam(value = "courseThumbnail", required = false) MultipartFile file,
                                                    @RequestHeader("Authorization") String token) throws IOException {
        CourseDTO updatedCourseDto = courseService.updateCourseOverview(courseId, token, description, file);
        return Response.success("강의실 개요 업데이트", updatedCourseDto);
    }

    // 강의실 삭제
    @DeleteMapping("/{courseId}")
    public Response<Void> deleteCourse(@PathVariable Long courseId, @RequestParam Long userId,
                                       @RequestHeader("Authorization") String token) {
        courseService.deleteCourse(courseId, token);
        return Response.success("강의실 삭제", null);
    }

    // 강의실 조회(설정 & 개요 페이지)
    @GetMapping("/{courseId}")
    public Response<CourseDTO> findByCourseId(@PathVariable Long courseId, @RequestHeader("Authorization") String token) {
        CourseDTO courseDTO = courseService.getCourseById(courseId);
        return Response.success("강의실 조회(설정&개요 페이지)", courseDTO);
    }

    // 강의실 입장
    @PostMapping("/enter/{userId}")
    public Response<?> enterCourse(
            @PathVariable Long userId,
            @RequestParam String entryCode,
            @RequestHeader("Authorization") String token) {
        // 서비스 메서드를 호출하여 강의실 입장 처리
        courseService.enterCourse(token, entryCode);

        // 성공 메시지 반환
        return Response.success("가입되었습니다!", null);
    }

    // 사용자가 가입한 강의실 목록 조회
    @GetMapping("/{userId}/my-courses")
    public Response<List<CourseDTO>> getCoursesByUser(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        List<CourseDTO> courses = courseService.getCoursesByUser(token);
        return Response.success("강의실 목록 조회", courses);
    }

}

