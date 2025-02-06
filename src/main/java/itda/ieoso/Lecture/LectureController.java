package itda.ieoso.Lecture;

import itda.ieoso.Course.Course;
import itda.ieoso.Response.DataResponse;
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
    public ResponseEntity<LectureDTO> createLecture(@PathVariable Long courseId,
                                                    @PathVariable Long userId,
                                                    @RequestBody Lecture lectureRequest) {
        // LectureService를 통해 강의 생성
        LectureDTO lectureDTO = lectureService.createLecture(courseId, userId, lectureRequest);
        System.out.println(lectureRequest);
        return ResponseEntity.ok(lectureDTO);
    }

    // 강의 수정
    @PutMapping("/{courseId}/{lectureId}/{userId}")
    public ResponseEntity<LectureDTO> updateLecture(@PathVariable Long courseId,
                                                    @PathVariable Long lectureId,
                                                    @PathVariable Long userId,
                                                    @RequestBody Lecture lectureRequest) {
        // 강의 수정 처리
        LectureDTO updatedLectureDTO = lectureService.updateLecture(courseId, lectureId, userId, lectureRequest);

        return ResponseEntity.ok(updatedLectureDTO); // 수정된 강의 반환
    }

    // 강의 삭제 (과정 생성자만 가능)
    @DeleteMapping("/{courseId}/{lectureId}/{userId}")
    public ResponseEntity<Void> deleteLecture(
            @PathVariable Long courseId,
            @PathVariable Long lectureId, // 강의 ID
            @PathVariable Long userId // 사용자 ID
    ) {
        // 강의가 속한 과정의 생성자인지 확인
        if (!lectureService.isCourseCreator(courseId, userId)) {
            return ResponseEntity.status(403).build(); // 권한 없음 (Forbidden)
        }

        // LectureService를 통해 강의 삭제
        lectureService.deleteLecture(courseId, lectureId, userId);
        return ResponseEntity.noContent().build();
    }

    // 강의 조회
    @GetMapping("/{courseId}/{lectureId}/{userId}")
    public ResponseEntity<LectureDTO> findByLectureId(@PathVariable Long courseId,
                                                      @PathVariable Long lectureId,
                                                      @PathVariable Long userId) {
        // 강의 정보 조회
        LectureDTO lectureDTO = lectureService.getLecture(courseId, lectureId, userId);
        return ResponseEntity.ok(lectureDTO); // 조회된 강의 반환
    }

    // 강의 목록 조회
    @GetMapping("/{courseId}/lectures/{userId}")
    public ResponseEntity<List<LectureDTO>> getLecturesByCourseId(@PathVariable Long courseId,
                                                                  @PathVariable Long userId) {
        // 서비스 레이어에서 강의 목록을 가져옴
        List<LectureDTO> lectures = lectureService.getLectureList(courseId,userId);

        // ResponseEntity로 반환
        return ResponseEntity.ok(lectures);
    }

    // ------------------ 강의실 관리 ----------------------

    // 강의실 커리큘럼 조회(userid = 수강생) TODO 개설자용 따로 만들기
    @GetMapping("/curriculum/{courseId}/{userId}")
    public DataResponse<List<CurriculumResponseDto>> getCurriculum(@PathVariable Long userId,
                                                                   @PathVariable Long courseId) {
        DataResponse<List<CurriculumResponseDto>> response = new DataResponse<>(lectureService.getCurriculum(userId,courseId));
        return response;
    }

    // 대시보드 조회(userid = 수강생)
    @GetMapping("/dashboard/{courseId}/{userId}")
    public DataResponse<?> getToDoList(@PathVariable Long courseId,
                                       @PathVariable Long userId,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime date) {
        // RequestParam 없음 = 전체조회 / 있음 = 해당날짜 조회
        DataResponse<?> response = new DataResponse<>(lectureService.getToDoList(courseId, userId, date));
        return response;
    }
}