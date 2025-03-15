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
                                                            @PathVariable Long userId) {

        return Response.success("과제 생성" , assignmentService.createAssignment(courseId,lectureId,userId));
    }

    @PatchMapping("/{courseId}/{assignmentId}/{userId}")
    public Response<AssignmentDTO.Response> updateAssignment(@PathVariable Long courseId,
                                         @PathVariable Long assignmentId,
                                         @PathVariable Long userId,
                                         @RequestBody AssignmentDTO.Request request) {
        return Response.success("과제 수정", assignmentService.updateAssignment(courseId,assignmentId,userId,request));
    }

    @DeleteMapping("/{courseId}/{assignmentId}/{userId}")
    public Response<AssignmentDTO.deleteResponse> deleteAssignment(@PathVariable Long courseId,
                                               @PathVariable Long assignmentId,
                                               @PathVariable Long userId) {
        return Response.success("과제 삭제", assignmentService.deleteAssignment(courseId,assignmentId,userId));
    }

    // 과제 조회
    @GetMapping("/{assignmentId}")
    public Response<AssignmentDTO.Response> getAssignment(
            @PathVariable Long assignmentId) {

        // 과제 정보를 가져와서 AssignmentDTO로 변환
        AssignmentDTO.Response assignmentDTO = assignmentService.getAssignment(assignmentId);

        return Response.success("과제 조회", assignmentDTO); // 조회한 과제 정보 반환
    }

}