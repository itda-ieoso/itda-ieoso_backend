package itda.ieoso.Assignment;

import itda.ieoso.Video.VideoDto;
import itda.ieoso.Video.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {
    @Autowired
    private AssignmentService assignmentService;

    @PostMapping("/{courseId}/{lectureId}/{userId}")
    public AssignmentDTO.Response createAssignment(@PathVariable Long courseId,
                                                   @PathVariable Long lectureId,
                                                   @PathVariable Long userId,
                                                   @RequestBody AssignmentDTO.createRequest request) {

        return assignmentService.createVideo(courseId,lectureId,userId,request);
    }

    @PatchMapping("/{courseId}/{assignmentId}/{userId}")
    public AssignmentDTO.Response updateAssignment(@PathVariable Long courseId,
                                         @PathVariable Long assignmentId,
                                         @PathVariable Long userId,
                                         @RequestBody AssignmentDTO.updateRequest request) {
        return assignmentService.updateVideo(courseId,assignmentId,userId,request);
    }

    @DeleteMapping("/{courseId}/{assignmentId}/{userId}")
    public AssignmentDTO.deleteResponse deleteAssignment(@PathVariable Long courseId,
                                               @PathVariable Long assignmentId,
                                               @PathVariable Long userId) {
        return assignmentService.deleteVideo(courseId,assignmentId,userId);
    }
}