package itda.ieoso.Lecture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lectures")
public class LectureController {
//
//    @Autowired
//    private LectureService lectureService;
//
//    // 강의 생성
//    @PostMapping("/{courseId}")
//    public ResponseEntity<Lecture> createLecture(
//            @PathVariable String courseId,
//            @RequestParam String userId,
//            @RequestParam String title,
//            @RequestParam(required = false) String description,
//            @RequestParam String videoLink) {
//
//        Lecture lecture = lectureService.createLecture(courseId, userId, title, description, videoLink);
//        return ResponseEntity.ok(lecture);
//    }
//
//    // 강의 삭제
//    @DeleteMapping("/{lectureId}")
//    public ResponseEntity<Void> deleteLecture(
//            @PathVariable String lectureId,
//            @RequestParam String userId) {
//
//        lectureService.deleteLecture(lectureId, userId);
//        return ResponseEntity.noContent().build();
//    }
//
//    // 강의 조회 (과정 ID로 강의 리스트 조회)
//    @GetMapping("/{courseId}")
//    public ResponseEntity<List<Lecture>> getLecturesByCourseId(@PathVariable String courseId) {
//        List<Lecture> lectures = lectureService.getLecturesByCourseId(courseId);
//        return ResponseEntity.ok(lectures);
//    }
}