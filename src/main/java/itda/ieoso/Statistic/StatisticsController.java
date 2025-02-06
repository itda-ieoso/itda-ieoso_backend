package itda.ieoso.Statistic;

import itda.ieoso.Response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired private StatisticsService statisticsService;

    // 과제 제출 통계 조회 API
    @GetMapping("/courses/{courseId}/assignments")
    public Response<List<AssignmentStatisticsDTO>> getAssignmentStatistics(@PathVariable Long courseId) {
        List<AssignmentStatisticsDTO> statistics = statisticsService.getAssignmentStatistics(courseId);
        return Response.success("통계 요약 조회", statistics); // 성공 시 데이터 반환
    }
}



