package itda.ieoso.Lecture;

import itda.ieoso.Course.Course;
import itda.ieoso.Response.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
//    @PostMapping("/{courseId}/{userId}")
//    public ResponseEntity<LectureDTO> createLecture(@PathVariable Long courseId, @PathVariable Long userId, @RequestBody Lecture lectureRequest) {
//        // LectureService를 통해 강의 생성
//        LectureDTO lectureDTO = lectureService.createLecture(courseId, userId, lectureRequest.getLectureTitle(),
//                lectureRequest.getLectureDescription(), lectureRequest.getStartDate(), lectureRequest.getEndDate());
//        System.out.println(lectureRequest);
//        return ResponseEntity.ok(lectureDTO);
//    }
//    // 강의 수정
//    @PutMapping("/{courseId}/{lectureId}/{userId}")
//    public ResponseEntity<LectureDTO> updateLecture(@PathVariable Long courseId, @PathVariable Long lectureId, @PathVariable Long userId, @RequestBody Lecture lectureRequest) {
//        // 강의 수정 처리
//        LectureDTO updatedLectureDTO = lectureService.updateLecture(
//                courseId,
//                lectureId,
//                userId,
//                lectureRequest.getLectureTitle(),
//                lectureRequest.getLectureDescription(),
//                lectureRequest.getStartDate(),
//                lectureRequest.getEndDate()
//        );
//
//        return ResponseEntity.ok(updatedLectureDTO); // 수정된 강의 반환
//    }
//
    // 강의 삭제 (과정 생성자만 가능)
//    @DeleteMapping("/{courseId}/{lectureId}")
//    public ResponseEntity<Void> deleteLecture(
//            @PathVariable Long courseId,
//            @PathVariable Long lectureId, // 강의 ID
//            @RequestParam Long userId // 사용자 ID
//    ) {
//        // 강의가 속한 과정의 생성자인지 확인
//        if (!lectureService.isLectureOwner(lectureId, userId)) {
//            return ResponseEntity.status(403).build(); // 권한 없음 (Forbidden)
//        }
//
//        // LectureService를 통해 강의 삭제
//        lectureService.deleteLecture(courseId, lectureId, userId);
//        return ResponseEntity.noContent().build();
//    }

    // 강의 조회
//    @GetMapping("/{courseId}/{lectureId}")
//    public ResponseEntity<LectureDTO> findByLectureId(@PathVariable Long courseId, @PathVariable Long lectureId) {
//        // 강의 정보 조회
//        LectureDTO lectureDTO = lectureService.getLectureById(lectureId);
//        return ResponseEntity.ok(lectureDTO); // 조회된 강의 반환
//    }

    // 특정 강의실에 속한 강의 목록 조회
//    @GetMapping("/{courseId}/lectures")
//    public ResponseEntity<List<LectureDTO>> getLecturesByCourseId(@PathVariable Long courseId) {
//        // 서비스 레이어에서 강의 목록을 가져옴
//        List<LectureDTO> lectures = lectureService.getLecturesByCourseId(courseId);
//
//        // ResponseEntity로 반환
//        return ResponseEntity.ok(lectures);
//    }

    // ------------------ 강의실 관리 ----------------------
    // 강의실 커리큘럼편집(userid = 편집자)
    @PostMapping("/curriculum/{userId}/{courseId}")
    public DataResponse<CurriculumModificationRequest> createCurriculum(@PathVariable Long userId,
                                                                   @PathVariable Long courseId,
                                                                   @RequestBody CurriculumModificationRequest dto) {
        DataResponse<CurriculumModificationRequest> response = new DataResponse<>(lectureService.createCurriculum(userId,courseId, dto));
        return response;
    }

    // 강의실 커리큘럼 조회(수강생용) TODO 개설자용 따로 만들기
    @GetMapping("/curriculum/{userId}/{courseId}")
    public DataResponse<List<CurriculumResponseDto>> getCurriculum(@PathVariable Long userId,
                                              @PathVariable Long courseId) {

        DataResponse<List<CurriculumResponseDto>> response = new DataResponse<>(lectureService.getCurriculum(userId,courseId));
        return response;
    }
}