package itda.ieoso.Submission;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.Assignment.AssignmentRepository;
import itda.ieoso.File.S3Service;
import itda.ieoso.User.UserDTO;
import itda.ieoso.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private S3Service s3Service;

    // 과제 제출 및 수정
    public SubmissionDTO updateSubmission(Long assignmentId, Long submissionId, Long userId, String textContent, MultipartFile[] files) throws IOException, IOException {
        // 과제 조회
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("과제를 찾을 수 없습니다"));

        // 제출 정보 조회
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("제출 정보를 찾을 수 없습니다"));

        // 제출한 사용자가 요청한 사용자 ID와 일치하는지 확인
        if (!submission.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("이 과제를 수정할 권한이 없습니다.");
        }

        // 파일 업로드
        String folder = "submissions";  // 업로드할 폴더를 "submissions"로 지정
        List<String> fileUrls = s3Service.uploadFiles(folder, files);  // MultipartFile[]로 파일 받기

        // 제출 상태 변경
        if(submission.getSubmissionStatus() == SubmissionStatus.NOT_SUBMITTED) {
            submission.setTextContent(textContent);
            submission.setFileUrls(fileUrls); // 여러 파일 URL 저장
            submission.setSubmittedAt(LocalDateTime.now());
            submission.setSubmissionStatus(assignment.getEndDate().isAfter(LocalDate.now()) ? SubmissionStatus.SUBMITTED : SubmissionStatus.LATE);
        } else {
            submission.setTextContent(textContent);
            submission.setFileUrls(fileUrls); // 여러 파일 URL 저장
            submission.setSubmittedAt(LocalDateTime.now());
        }

        // 수정된 제출 정보 저장
        submissionRepository.save(submission);

        // UserDTO.UserInfoDto 생성
        UserDTO.UserInfoDto userInfoDto = UserDTO.UserInfoDto.of(submission.getUser(), submission.getUser().getProfileImageUrl());

        // 수정된 SubmissionDTO 반환
        return SubmissionDTO.of(submission, userInfoDto);
    }

    // 과제 삭제
    public void deleteSubmission(Long assignmentId, Long submissionId, Long userId) {
        // 과제 조회
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("과제를 찾을 수 없습니다"));

        // 제출 정보 조회
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("제출 정보를 찾을 수 없습니다"));

        // 제출한 사용자가 요청한 사용자 ID와 일치하는지 확인
        if (!submission.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("이 과제를 삭제할 권한이 없습니다.");
        }

        // 제출 정보 삭제
        submission.setTextContent(null);
        submission.setFileUrls(null);
        submission.setSubmittedAt(null);
        submission.setSubmissionStatus(SubmissionStatus.NOT_SUBMITTED);

        submissionRepository.save(submission);
    }

    // 과제 조회
    public SubmissionDTO getSubmission(Long assignmentId, Long submissionId, Long userId) {
        // 과제 조회
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("과제를 찾을 수 없습니다"));

        // 제출 정보 조회
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("제출 정보를 찾을 수 없습니다"));

        // 제출한 사용자가 요청한 사용자 ID와 일치하는지 확인
        if (!submission.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("이 과제를 조회할 권한이 없습니다.");
        }

        // UserDTO.UserInfoDto 생성
        UserDTO.UserInfoDto userInfoDto = UserDTO.UserInfoDto.of(submission.getUser(), submission.getUser().getProfileImageUrl());

        // SubmissionDTO로 변환하여 반환
        return SubmissionDTO.of(submission, userInfoDto);
    }
}
