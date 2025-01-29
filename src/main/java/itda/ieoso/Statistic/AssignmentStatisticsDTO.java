package itda.ieoso.Statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentStatisticsDTO {

    // 과제 ID
    private Long assignmentId;

    // 과제 제목
    private String assignmentTitle;

    // 학생별 제출 상태 리스트
    private List<StudentSubmissionStatus> studentStatuses;

    /**
     * 학생별 제출 상태를 표현하는 내부 클래스
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentSubmissionStatus {
        private Long userId;           // 학생 ID
        private String studentName;    // 학생 이름
        private String status;         // 제출 상태 ("SUBMITTED", "NOT_SUBMITTED", "LATE")
    }
}

