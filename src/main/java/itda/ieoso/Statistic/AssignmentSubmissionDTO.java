package itda.ieoso.Statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentSubmissionDTO {

    // 과제 ID
    private Long assignmentId;

    // 과제 제목
    private String assignmentTitle;

    // 학생별 제출 결과 리스트
    private List<StudentSubmissionResult> studentResults;

    /**
     * 학생별 제출 결과를 표현하는 내부 클래스
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentSubmissionResult {
        private Long userId;        // 학생 ID
        private String studentName; // 학생 이름
        private String fileName;    // 제출된 파일 이름
        private String fileUrl;     // 제출된 파일 URL
        private LocalDateTime submittedAt; // 제출 일시
        private String status;      // 제출 상태 ("SUBMITTED", "NOT_SUBMITTED", "LATE")
    }
}
