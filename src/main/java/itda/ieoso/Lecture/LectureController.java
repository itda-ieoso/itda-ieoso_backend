package itda.ieoso.Lecture;

import itda.ieoso.ContentOrder.ContentOrderDto;
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
import java.util.Set;

@RestController
@RequestMapping("/lectures")
public class LectureController {

    @Autowired
    private LectureService lectureService;
    private LectureRepository lectureRepository;

    // 강의 생성 (개설자용)
    @PostMapping("/{courseId}/{userId}")
    public Response<LectureDTO.Response> createLecture(@PathVariable Long courseId,
                                              @PathVariable Long userId,
                                              @RequestBody LectureDTO.Request request, @RequestHeader("Authorization") String token) {
        return Response.success("강의 생성", lectureService.createLecture(courseId, token, request));
    }

    // 강의 수정 (개설자용)
    @PatchMapping("/{courseId}/{lectureId}/{userId}")
    public Response<LectureDTO.Response> updateLecture(@PathVariable Long courseId,
                                                    @PathVariable Long lectureId,
                                                    @PathVariable Long userId,
                                                    @RequestBody LectureDTO.Request request, @RequestHeader("Authorization") String token) {
        return Response.success("강의 수정", lectureService.updateLecture(courseId, lectureId, token, request));
    }

    // 강의 삭제 (개설자용)
    @DeleteMapping("/{courseId}/{lectureId}/{userId}")
    public Response<LectureDTO.deleteResponse> deleteLecture(
            @PathVariable Long courseId,
            @PathVariable Long lectureId,
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token
    ) {
        // 강의가 속한 과정의 생성자인지 확인
        if (!lectureService.isCourseCreator(courseId, userId)) {
            return Response.fail("강의 삭제 실패"); // 권한 없음 (Forbidden)
        }

        // LectureService를 통해 강의 삭제
        return Response.success("강의 삭제",lectureService.deleteLecture(courseId, lectureId, token));
    }

    // 강의실 커리큘럼 전체조회 (개설자, 수강생용)
    @GetMapping("/curriculum/{courseId}/{userId}")
    public Response<List<LectureDTO.CurriculumResponse>> getLectureList(@PathVariable Long userId,
                                                                   @PathVariable Long courseId, @RequestHeader("Authorization") String token) {
        return Response.success("커리큘럼 전체 조회", lectureService.getLectureList(courseId, token));
    }

    // 강의수강상태 조회(수강생용)
    @GetMapping("/history/{courseId}/{userId}")
    public Response<LectureDTO.HistoryResponse> getLectureHistories(@PathVariable Long courseId,
                                                          @PathVariable Long userId, @RequestHeader("Authorization") String token) {

        return Response.success("수강생 히스토리 조회", lectureService.getLectureHistories(courseId, token));
    }

    // 대시보드 조회(userid = 수강생)
    @GetMapping("/dashboard/today/{userId}")
    public Response<?> getTodayDashboard(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        return Response.success("대시보드 조회(할일 목록)",lectureService.getDayTodoList(token, LocalDateTime.now()));
    }

//    @GetMapping("/dashbard/week/{userId}")
//    public Response<?> getWeeklyDashboard(@PathVariable Long userId) {
//        return Response.success("대시보드 조회(이번주)" , lectureService.getWeekTodoList(userId, LocalDateTime.now()));
//    }
}