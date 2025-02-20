package itda.ieoso.Submission;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.Assignment.AssignmentRepository;
import itda.ieoso.Course.Course;
import itda.ieoso.Exception.CustomException;
import itda.ieoso.Exception.ErrorCode;
import itda.ieoso.File.S3Service;
import itda.ieoso.User.UserDTO;
import itda.ieoso.User.UserRepository;
import itda.ieoso.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubmissionFileRepository submissionFileRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private UserService userService;

    @Transactional
    // 과제 제출 및 수정
    public SubmissionDTO updateSubmission(Long assignmentId, Long submissionId, String token, String textContent, List<String> existingFileUrls,
                                          List<String> deleteFileUrls, MultipartFile[] newFiles) throws IOException, IOException {
        Long userId = userService.getUserByToken(token).getUserId();
        // 과제 조회
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        // 제출 정보 조회
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBMISSION_NOT_FOUND));

        // 제출한 사용자가 요청한 사용자 ID와 일치하는지 확인
        if (!submission.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.SUBMISSION_PERMISSION_DENIED);
        }

        //기존 파일 유지 (existingFileUrls에 포함된 파일만 유지)
        if (existingFileUrls != null) {
            submission.setSubmissionFiles(
                    submission.getSubmissionFiles().stream()
                            .filter(file -> existingFileUrls.contains(file.getSubmissionFileUrl()))
                            .collect(Collectors.toList())
            );
        }

        // 삭제할 파일 처리 (DB에서만 삭제)
        if (deleteFileUrls != null) {
            submissionFileRepository.deleteBySubmissionFileUrlIn(deleteFileUrls);

            // 메모리에서도 삭제
            submission.setSubmissionFiles(
                    submission.getSubmissionFiles().stream()
                            .filter(file -> !deleteFileUrls.contains(file.getSubmissionFileUrl())) // 삭제 리스트에 없는 파일만 유지
                            .collect(Collectors.toList())
            );
        }

        if (newFiles != null) {
            // 파일 업로드
            String folder = "submissions";  // 업로드할 폴더를 "submissions"로 지정
            List<String> newFileUrls = s3Service.uploadFiles(folder, newFiles);  // MultipartFile[]로 파일 받기

            for (int i = 0; i < newFiles.length; i++) {
                MultipartFile file = newFiles[i];
                SubmissionFile submissionFile = SubmissionFile.createFile(
                        newFileUrls.get(i),
                        file.getOriginalFilename(),
                        formatFileSize(file.getSize()),
                        submission
                );
                submission.getSubmissionFiles().add(submissionFile);
            }
        }

        submission.setTextContent(textContent);
        submission.setSubmittedAt(LocalDateTime.now());

        if (submission.getSubmissionStatus() == SubmissionStatus.NOT_SUBMITTED) {
            submission.setSubmissionStatus(
                    assignment.getEndDate().isAfter(LocalDateTime.now()) ? SubmissionStatus.SUBMITTED : SubmissionStatus.LATE
            );
        }

        // 수정된 제출 정보 저장
        submissionRepository.save(submission);

        // UserDTO.UserInfoDto 생성
        UserDTO.UserInfoDto userInfoDto = UserDTO.UserInfoDto.of(submission.getUser(), submission.getUser().getProfileImageUrl());

        // 수정된 SubmissionDTO 반환
        return SubmissionDTO.of(submission, userInfoDto);
    }

    // 파일 크기 포맷팅 유틸리티 메서드 (KB, MB, GB)
    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1048576) { // 1024 * 1024
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1073741824) { // 1024 * 1024 * 1024
            return String.format("%.2f MB", bytes / 1048576.0);
        } else {
            return String.format("%.2f GB", bytes / 1073741824.0);
        }
    }

    public Submission getSubmissionById(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBMISSION_NOT_FOUND));
    }

    @Transactional
    // 과제 삭제
    public void deleteSubmission(Long assignmentId, Long submissionId, String token) {
        Long userId = userService.getUserByToken(token).getUserId();
        // 과제 조회
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        // 제출 정보 조회
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBMISSION_NOT_FOUND));

        // 제출한 사용자가 요청한 사용자 ID와 일치하는지 확인
        if (!submission.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.SUBMISSION_PERMISSION_DENIED);
        }

        // 제출 정보 삭제
        submission.setTextContent(null);
        submission.setSubmittedAt(null);
        submission.setSubmissionStatus(SubmissionStatus.NOT_SUBMITTED);

        // 제출 파일 삭제 (DB에서만 삭제)
        List<String> fileUrlsToDelete = submission.getSubmissionFiles().stream()
                .map(SubmissionFile::getSubmissionFileUrl)  // 파일 URL 리스트 추출
                .collect(Collectors.toList());

        if (!fileUrlsToDelete.isEmpty()) {
            submissionFileRepository.deleteBySubmissionFileUrlIn(fileUrlsToDelete);  // 파일 URL들로 DB에서 삭제
        }

        // 파일 리스트를 초기화 (이미 DB에서 삭제했으므로 비워도 됨)
        submission.setSubmissionFiles(new ArrayList<>());

        submissionRepository.save(submission);
    }

    // 과제 조회
    public SubmissionDTO getSubmission(Long assignmentId, Long submissionId, String token) {
        Long userId = userService.getUserByToken(token).getUserId();
        // 과제 조회
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        // 제출 정보 조회
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBMISSION_NOT_FOUND));

        Course course = assignment.getCourse();
        Long courseOwnerId = course.getUser().getUserId();

        // 제출한 사용자가 요청한 사용자 ID와 일치하는지 확인
        if (!submission.getUser().getUserId().equals(userId) && !courseOwnerId.equals(userId)) {
            throw new CustomException(ErrorCode.SUBMISSION_PERMISSION_DENIED);
        }  //수정 예정-조회관련

        // UserDTO.UserInfoDto 생성
        UserDTO.UserInfoDto userInfoDto = UserDTO.UserInfoDto.of(submission.getUser(), submission.getUser().getProfileImageUrl());

        // SubmissionDTO로 변환하여 반환
        return SubmissionDTO.of(submission, userInfoDto);
    }
}
