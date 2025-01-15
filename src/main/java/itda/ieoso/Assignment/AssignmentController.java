package itda.ieoso.Assignment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/{courseId}/{lectureId}/assignments")
public class AssignmentController {
//
//    private final AssignmentService assignmentService;
//
//    public AssignmentController(AssignmentService assignmentService) {
//        this.assignmentService = assignmentService;
//    }
//
//    // 과제 생성
//    @PostMapping
//    public ResponseEntity<Void> createAssignment(
//            @PathVariable String courseId,
//            @PathVariable String lectureId,
//            @RequestParam String title,
//            @RequestParam(required = false) String description,
//            @RequestParam Date dueDate,
//            @RequestParam String userId
//    ) {
//        assignmentService.createAssignment(courseId, lectureId, userId, title, description, dueDate);
//        return ResponseEntity.ok().build();
//    }
//    // 과제 수정 (Update Assignment)
//    @PutMapping("/{assignmentId}")
//    public ResponseEntity<Void> updateAssignment(
//            @PathVariable String courseId,
//            @PathVariable String lectureId,
//            @PathVariable String assignmentId,
//            @RequestParam(required = false) String title,
//            @RequestParam(required = false) String description,
//            @RequestParam(required = false) Date dueDate
//    ) {
//        assignmentService.updateAssignment(courseId, lectureId, assignmentId, title, description, dueDate);
//        return ResponseEntity.ok().build();
//    }
//
//    // 과제 삭제 (Delete Assignment)
//    @DeleteMapping("/{assignmentId}")
//    public ResponseEntity<Void> deleteAssignment(
//            @PathVariable String courseId,
//            @PathVariable String lectureId,
//            @PathVariable String assignmentId
//    ) {
//        assignmentService.deleteAssignment(courseId, lectureId, assignmentId);
//        return ResponseEntity.ok().build();
//    }
//
//    // 과제 조회 (Get Assignment Details)
//    @GetMapping("/{assignmentId}")
//    public ResponseEntity<Assignment> getAssignment(
//            @PathVariable String courseId,
//            @PathVariable String lectureId,
//            @PathVariable String assignmentId
//    ) {
//        Assignment assignment = assignmentService.getAssignment(courseId, lectureId, assignmentId);
//        return ResponseEntity.ok(assignment);
//    }
}