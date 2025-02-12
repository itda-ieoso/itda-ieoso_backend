package itda.ieoso.Lecture;

import itda.ieoso.Course.Course;
import itda.ieoso.Response.DataResponse;
import itda.ieoso.Response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/lectures")
public class LectureController {

    @Autowired
    private LectureService lectureService;
    private LectureRepository lectureRepository;

    // 강의 생성 (과정 생성자만 가능)
    @PostMapping("/{courseId}/{userId}")
    public Response<LectureDTO> createLecture(@PathVariable Long courseId,
                                              @PathVariable Long userId,
                                              @RequestBody Lecture lectureRequest) {
        return Response.success("강의 생성", lectureService.createLecture(courseId, userId, lectureRequest));
    }

    // 강의 수정
    @PutMapping("/{courseId}/{lectureId}/{userId}")
    public Response<LectureDTO> updateLecture(@PathVariable Long courseId,
                                                    @PathVariable Long lectureId,
                                                    @PathVariable Long userId,
                                                    @RequestBody Lecture lectureRequest) {
        return Response.success("강의 수정", lectureService.updateLecture(courseId, lectureId, userId, lectureRequest));
    }

    // 강의 삭제 (과정 생성자만 가능)
    @DeleteMapping("/{courseId}/{lectureId}/{userId}")
    public Response<?> deleteLecture(
            @PathVariable Long courseId,
            @PathVariable Long lectureId, // 강의 ID
            @PathVariable Long userId // 사용자 ID
    ) {
        // 강의가 속한 과정의 생성자인지 확인
        if (!lectureService.isCourseCreator(courseId, userId)) {
            return Response.fail("강의 삭제 실패"); // 권한 없음 (Forbidden)
        }

        // LectureService를 통해 강의 삭제
        lectureService.deleteLecture(courseId, lectureId, userId);
        return Response.success("강의 삭제",null);
    }

    // 강의 조회
    @GetMapping("/{courseId}/{lectureId}/{userId}")
    public Response<LectureDTO> findByLectureId(@PathVariable Long courseId,
                                                      @PathVariable Long lectureId,
                                                      @PathVariable Long userId) {
        return Response.success("강의 조회", lectureService.getLecture(courseId, lectureId, userId));
    }

    // 강의 목록 조회
    @GetMapping("/{courseId}/lectures/{userId}")
    public Response<List<LectureDTO>> getLecturesByCourseId(@PathVariable Long courseId,
                                                                  @PathVariable Long userId) {
        return Response.success("강의 목록 조회", lectureService.getLectureList(courseId,userId));
    }

    // ------------------ 강의실 관리 ----------------------

    // 강의실 커리큘럼 조회(userid = 수강생) TODO 개설자용 따로 만들기
    @GetMapping("/curriculum/{courseId}/{userId}")
    public Response<List<CurriculumResponseDto>> getCurriculum(@PathVariable Long userId,
                                                                   @PathVariable Long courseId) {
        return Response.success("커리큘럼 전체 조회", lectureService.getCurriculum(courseId, userId));
    }

    // 대시보드 조회(userid = 수강생)
    @GetMapping("/dashboard/{courseId}/{userId}")
    public Response<?> getToDoList(@PathVariable Long courseId,
                                       @PathVariable Long userId,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime date) {
        return Response.success("대시보드 조회(할일 목록)",lectureService.getToDoList(courseId, userId, date));
    }
}