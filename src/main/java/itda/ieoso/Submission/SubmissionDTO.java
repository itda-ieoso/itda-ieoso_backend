package itda.ieoso.Submission;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.User.UserDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SubmissionDTO {

    private Long submissionId;
    private UserDTO.UserInfoDto user;
    private String textContent;
    private String fileUrl;
    private SubmissionStatus submissionStatus;
    private LocalDateTime submittedAt;
    private boolean gradeStatus;
    private int score;

    // Submission과 UserInfoDto를 결합하여 반환
    public static SubmissionDTO of(Submission submission, UserDTO.UserInfoDto userInfoDto) {
        return SubmissionDTO.builder()
                .submissionId(submission.getSubmissionId())
                .textContent(submission.getTextContent())
                .fileUrl(submission.getFileUrl())
                .submissionStatus(submission.getSubmissionStatus())
                .submittedAt(submission.getSubmittedAt())
                .gradeStatus(submission.isGradeStatus())
                .score(submission.getScore())
                .user(userInfoDto)
                .build();
    }
}

