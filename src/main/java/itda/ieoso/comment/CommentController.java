package itda.ieoso.comment;

import itda.ieoso.Response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static itda.ieoso.comment.CommentDto.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // 댓글 전체조회
    @GetMapping("/{submissionId}")
    public Response<?> getComments(@PathVariable Long submissionId) {

        return Response.success("댓글 조회", commentService.getComments(submissionId));
    }

    // 댓글 작성
    @PostMapping("/{submissionId}")
    public Response<?> createComment(@PathVariable Long submissionId,
                                     @RequestBody commentRequestDto requestDto) {

        commentService.createComment(submissionId, requestDto);
        return Response.success("댓글 작성", null);
    }

    // 답글 작성
    @PostMapping("/{submissionId}/{commentId}/replies")
    public Response<?> addReply(@PathVariable Long submissionId,
                                @PathVariable Long commentId,
                                @RequestBody commentRequestDto requestDto) {

        commentService.addReply(submissionId, commentId, requestDto);
        return Response.success("답글 작성", null);
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public Response<?> updateComment(@PathVariable Long commentId,
                                     @RequestBody commentRequestDto requestDto) {

        commentService.updateComment(commentId, requestDto);
        return Response.success("댓글 수정", null);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public Response<?> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return Response.success("댓글 삭제", null);
    }


}
