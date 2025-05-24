package itda.ieoso.comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentDto {
    public record commentRequestDto(
            String content
    ) {
    }

    public record commentsResponseDto(
            Long userId,
            String role,
            String userName,
            Long commentId,
            String content,
            LocalDateTime createdAt,
            List<CommentDto.commentsResponseDto> children
    ) {
        public static commentsResponseDto of(Comment comment) {
            // 삭제 처리된 경우엔 이름과 내용 삭제
            String content = comment.isDeleted() ? "삭제된 댓글입니다." : comment.getContent();

            return new commentsResponseDto(
                    comment.getUser().getUserId(),
                    comment.getRole(),
                    comment.getUser().getName(),
                    comment.getCommentId(),
                    content,
                    comment.getCreatedAt(),
                    new ArrayList<>()
            );
        }
    }
}
