package itda.ieoso.Submission;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.Assignment.AssignmentRepository;
import itda.ieoso.Course.Course;
import itda.ieoso.Exception.CustomException;
import itda.ieoso.Exception.ErrorCode;
import itda.ieoso.File.S3Service;
import itda.ieoso.User.User;
import itda.ieoso.User.UserDTO;
import itda.ieoso.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // SecurityContext에서 현재 사용자 조회
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // 현재 로그인한 사용자의 이메일 가져오기
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    // 과제 제출 및 수정
    public SubmissionDTO updateSubmission(Long assignmentId, Long submissionId, Long userId, String textContent, List<String> existingFileUrls,
                                          List<String> deleteFileUrls, MultipartFile[] newFiles) throws IOException, IOException {
        User authenticatedUser = getAuthenticatedUser();
        // 과제 조회
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        // 제출 정보 조회
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBMISSION_NOT_FOUND));

        // 제출한 사용자가 요청한 사용자 ID와 일치하는지 확인
        if (!submission.getUser().getUserId().equals(authenticatedUser.getUserId())) {
            throw new CustomException(ErrorCode.SUBMISSION_PERMISSION_DENIED);
        }

        // TODO assignment의 submissionType에 따라 제출방식 제한

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
            // S3 상에서 파일 이동
            for (String fileUrl : deleteFileUrls) {
                s3Service.moveFileToDeleteFolder(fileUrl); // "delete" 폴더로 이동
            }
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
        } else if (submission.getSubmissionStatus() == SubmissionStatus.SUBMITTED && assignment.getEndDate().isBefore(LocalDateTime.now())) {
            submission.setSubmissionStatus(SubmissionStatus.LATE);
        }

        // 수정된 제출 정보 저장
        submissionRepository.save(submission);

        // UserDTO.UserInfoDto 생성
        UserDTO.UserInfoDto userInfoDto = UserDTO.UserInfoDto.of(submission.getUser(), submission.getUser().getProfileImageUrl());

        // 수정된 SubmissionDTO 반환
        return SubmissionDTO.of(submission, userInfoDto, s3Service);
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
    public void deleteSubmission(Long assignmentId, Long submissionId, Long userId) {
        User authenticatedUser = getAuthenticatedUser();
        // 과제 조회
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        // 제출 정보 조회
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBMISSION_NOT_FOUND));

        // 제출한 사용자가 요청한 사용자 ID와 일치하는지 확인
        if (!submission.getUser().getUserId().equals(authenticatedUser.getUserId())) {
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
            // S3에서 "delete" 폴더로 이동
            for (String fileUrl : fileUrlsToDelete) {
                s3Service.moveFileToDeleteFolder(fileUrl);
            }
            submissionFileRepository.deleteBySubmissionFileUrlIn(fileUrlsToDelete);  // 파일 URL들로 DB에서 삭제
        }

        // 파일 리스트를 초기화 (이미 DB에서 삭제했으므로 비워도 됨)
        submission.setSubmissionFiles(new ArrayList<>());

        submissionRepository.save(submission);
    }

    // 과제 조회
    public SubmissionDTO getSubmission(Long assignmentId, Long submissionId, Long userId) {
        User authenticatedUser = getAuthenticatedUser();
        // 과제 조회
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        // 제출 정보 조회
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBMISSION_NOT_FOUND));

        Course course = assignment.getCourse();
        Long courseOwnerId = course.getUser().getUserId();

        // 제출한 사용자가 요청한 사용자 ID와 일치하는지 확인
        if (!submission.getUser().getUserId().equals(authenticatedUser.getUserId()) && !courseOwnerId.equals(authenticatedUser.getUserId())) {
            throw new CustomException(ErrorCode.SUBMISSION_PERMISSION_DENIED);
        }  //수정 예정-조회관련

        // UserDTO.UserInfoDto 생성
        UserDTO.UserInfoDto userInfoDto = UserDTO.UserInfoDto.of(submission.getUser(), submission.getUser().getProfileImageUrl());

        // SubmissionDTO로 변환하여 반환
        return SubmissionDTO.of(submission, userInfoDto, s3Service);
    }


    // 과제제출 상태변경(개설자용)
    @Transactional
    public void updateSubmissionStatus(Long assignmentId, Long submissionId, SubmissionStatus status) {
        User authenticatedUser = getAuthenticatedUser();

        // 과제 조회
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        // 제출 정보 조회
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBMISSION_NOT_FOUND));

        // 로그인 유저가 강의 개설자인지 검증
        if (!submission.getCourse().getUser().getUserId().equals(authenticatedUser.getUserId())) {
            throw new CustomException(ErrorCode.COURSE_PERMISSION_DENIED);
        }

        submission.setSubmissionStatus(status);
        submissionRepository.save(submission);

    }
}
