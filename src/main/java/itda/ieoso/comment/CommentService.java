package itda.ieoso.comment;

import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.Exception.CustomException;
import itda.ieoso.Exception.ErrorCode;
import itda.ieoso.Submission.Submission;
import itda.ieoso.Submission.SubmissionRepository;
import itda.ieoso.User.User;
import itda.ieoso.User.UserRepository;
import itda.ieoso.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static itda.ieoso.comment.CommentDto.*;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final CourseAttendeesRepository courseAttendeesRepository;


    public List<commentsResponseDto> getComments(Long submissionId) {
        // 로그인 정보 조회
        User user = getAuthenticatedUser();

        // submission조회
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBMISSION_NOT_FOUND));

        // 강의 접근 권한 검증
        CourseAttendees courseAttendees = courseAttendeesRepository.findByCourseAndUser(submission.getCourse(), user)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSEATTENDEES_PERMISSION_DENIED));

        // ----------------------
        // submission의 모든 댓글 시간 순서로 조회
        List<Comment> allComments = commentRepository.findAllBySubmissionOrderByCreatedAtAsc(submission);

        // comment전체를 dto로 변환후 map에 저장
        Map<Long, commentsResponseDto> dtoMap = new HashMap<>();
        List<commentsResponseDto> roots = new ArrayList<>();

        for (Comment comment : allComments) {
            // dto로 변환
            commentsResponseDto dto = commentsResponseDto.of(comment);

            // map에 추가
            dtoMap.put(comment.getCommentId(), dto);

            if (comment.getParent() == null) { // 최상단 댓글 일 경우
                // 최상위 댓글을 root 리스트에 저장
                roots.add(dto);
            } else { // 대댓글 일 경우
                // 대댓글 -> 부모 DTO안의 chidren리스트에 추가
                commentsResponseDto parentDto = dtoMap.get(comment.getParent().getCommentId());
                if (parentDto != null) {
                    parentDto.children().add(dto);
                }
            }
        }

        return roots;

    }

    @Transactional
    public void createComment(Long submissionId, commentRequestDto requestDto) {
        // 로그인 정보 조회
        User user = getAuthenticatedUser();

        // submission조회
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBMISSION_NOT_FOUND));

        // 강의 접근 권한 검증
        CourseAttendees courseAttendees = courseAttendeesRepository.findByCourseAndUser(submission.getCourse(), user)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSEATTENDEES_PERMISSION_DENIED));

        // 댓글 작성자의 역할(작성자, 개설자, 일반수강생) 구분
        String role = "";
        if (user.getUserId().equals(submission.getCourse().getUser().getUserId())) {
            role = "[Instructor]";
        } else if (user.getUserId().equals(submission.getUser().getUserId())) {
            role = "[Author]";
        } else {
            role = "[Student]";
        }

        // comment객제생성
        Comment comment = Comment.builder()
                .parent(null)
                .submission(submission)
                .user(user)
                .role(role)
                .content(requestDto.content())
                .build();

        // 저장
        commentRepository.save(comment);
    }

    @Transactional
    public void addReply(Long submissionId, Long commentId, commentRequestDto requestDto) {
        // 로그인 정보 조회
        User user = getAuthenticatedUser();

        // submission조회
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBMISSION_NOT_FOUND));

        // 강의 접근 권한 검증
        CourseAttendees courseAttendees = courseAttendeesRepository.findByCourseAndUser(submission.getCourse(), user)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSEATTENDEES_PERMISSION_DENIED));

        // 상위 댓글 조회
        Comment parent = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 댓글 작성자의 역할(작성자, 개설자, 일반수강생) 구분
        String role = "";
        if (user.getUserId().equals(submission.getCourse().getUser().getUserId())) {
            role = "[Instructor]";
        } else if (user.getUserId().equals(submission.getUser().getUserId())) {
            role = "[Author]";
        } else {
            role = "[Student]";
        }

        // comment객제생성
        Comment comment = Comment.builder()
                .parent(parent)
                .submission(submission)
                .user(user)
                .role(role)
                .content(requestDto.content())
                .build();

        // 저장
        commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(Long commentId, commentRequestDto requestDto) {
        // 로그인 정보 조회
        User user = getAuthenticatedUser();

        // 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 댓글 수정 권한 조회
        if (!comment.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }

        // 댓글 수정
        comment.updateContent(requestDto.content());

        // 저장
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        // 로그인 정보 조회
        User user = getAuthenticatedUser();

        // 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 댓글 수정 권한 조회
        if (!comment.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }

        // 삭제 처리(실제 삭제시 트리구조 오류 발생 -> 삭제된댓글도 유지처리)
        comment.softDelete();
        commentRepository.save(comment);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // 현재 로그인한 사용자의 이메일 가져오기
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
