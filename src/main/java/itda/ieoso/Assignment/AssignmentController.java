package itda.ieoso.Assignment;

import itda.ieoso.Response.Response;
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
    public Response<AssignmentDTO.Response> createAssignment(@PathVariable Long courseId,
                                                            @PathVariable Long lectureId,
                                                            @PathVariable Long userId,
                                                            @RequestBody AssignmentDTO.createRequest request) {

        return Response.success("과제 생성" , assignmentService.createVideo(courseId,lectureId,userId,request));
    }

    @PatchMapping("/{courseId}/{assignmentId}/{userId}")
    public Response<AssignmentDTO.Response> updateAssignment(@PathVariable Long courseId,
                                         @PathVariable Long assignmentId,
                                         @PathVariable Long userId,
                                         @RequestBody AssignmentDTO.updateRequest request) {
        return Response.success("과제 수정", assignmentService.updateVideo(courseId,assignmentId,userId,request));
    }

    @DeleteMapping("/{courseId}/{assignmentId}/{userId}")
    public Response<AssignmentDTO.deleteResponse> deleteAssignment(@PathVariable Long courseId,
                                               @PathVariable Long assignmentId,
                                               @PathVariable Long userId) {
        return Response.success("과제 삭제", assignmentService.deleteVideo(courseId,assignmentId,userId));
    }
}