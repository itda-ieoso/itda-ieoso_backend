package itda.ieoso.Submission;

import itda.ieoso.File.S3Service;
import itda.ieoso.MaterialHistory.MaterialHistory;
import itda.ieoso.MaterialHistory.MaterialHistoryDto;
import itda.ieoso.User.UserDTO;
import lombok.Builder;
import lombok.Getter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SubmissionDTO {

    private Long submissionId;
    private UserDTO.UserInfoDto user;
    private String textContent;
    private List<String> fileUrls; // 파일 URL 목록
    private List<String> fileNames; // 파일 원래 이름 목록
    private List<String> fileSizes; // 파일 크기 목록 (KB, MB, GB)
    private List<String> presignedUrls; // 미리보기용 presigned URL
    private SubmissionStatus submissionStatus;
    private LocalDateTime submittedAt;
    private boolean gradeStatus;
    private int score;

    // Submission과 UserInfoDto를 결합하여 반환
    public static SubmissionDTO of(Submission submission, UserDTO.UserInfoDto userInfoDto, S3Service s3Service) {
        // 파일 정보들을 SubmissionFile에서 가져옴
        List<String> fileUrls = submission.getSubmissionFiles().stream()
                .map(SubmissionFile::getSubmissionFileUrl)
                .toList();

        List<String> fileNames = submission.getSubmissionFiles().stream()
                .map(SubmissionFile::getSubmissionOriginalFilename)
                .toList();

        List<String> fileSizes = submission.getSubmissionFiles().stream()
                .map(SubmissionFile::getSubmissionFileSize)
                .toList();

        List<String> presignedUrls = submission.getSubmissionFiles().stream()
                .map(file -> {
                    try {
                        return s3Service.generatePresignedUrl(file.getSubmissionFileUrl());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        return SubmissionDTO.builder()
                .submissionId(submission.getSubmissionId())
                .textContent(submission.getTextContent())
                .fileUrls(fileUrls)
                .fileNames(fileNames) // 원래 파일 이름 추가
                .fileSizes(fileSizes) // 파일 크기 추가
                .presignedUrls(presignedUrls)
                .submissionStatus(submission.getSubmissionStatus())
                .submittedAt(submission.getSubmittedAt())
                .gradeStatus(submission.isGradeStatus())
                .score(submission.getScore())
                .user(userInfoDto)
                .build();
    }

    public record Response(
            Long assignmentId,
            Long submissionId,
            SubmissionStatus submissionStatus
    ) {
        public static Response of(Submission submission) {
            return new Response(
                    submission.getAssignment().getAssignmentId(),
                    submission.getSubmissionId(),
                    submission.getSubmissionStatus()
            );
        }
    }
}


