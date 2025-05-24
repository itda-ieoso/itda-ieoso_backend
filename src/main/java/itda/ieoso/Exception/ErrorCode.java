package itda.ieoso.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    ID_DUPLICATED(HttpStatus.CONFLICT, "중복된 아이디입니다"),
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "중복된 이메일입니다"),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "유저 인증에 실패했습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다. 토큰 재발행을 요청해주세요"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다. 로그인을 다시 해주세요."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "인증에 필요한 JWT가 없습니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다."),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "파일이 비어있습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "강의실을 찾을 수 없습니다."),
    LECTURE_NOT_FOUND(HttpStatus.NOT_FOUND, "강의를 찾을 수 없습니다."),
    MATERIAL_NOT_FOUND(HttpStatus.NOT_FOUND, "강의자료를 찾을 수 없습니다."),
    VIDEO_NOT_FOUND(HttpStatus.NOT_FOUND, "영상을 찾을 수 없습니다."),
    MATERIALHISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "강의자료 히스토리를 찾을 수 없습니다."),
    COURSE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "강의실의 개설자가 아닙니다."),
    ASSIGNMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "과제를 찾을 수 없습니다."),
    SUBMISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "제출 정보를 찾을 수 없습니다."),
    SUBMISSION_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "과제에 대한 권한이 없습니다."),
    ALREADY_ENROLLED(HttpStatus.CONFLICT, "이미 강의실에 등록되어 있습니다."),
    ANNOUNCEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "공지를 찾을 수 없습니다."),
    COURSEATTENDEES_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "강의실 접근권한이 없습니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "시간설정이 범위를 벗어났습니다."),
    INVALID_DAY_OF_WEEK(HttpStatus.BAD_REQUEST, "요일 범위설정을 벗어났습니다."),
    CONTENTORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "순서도메인을 찾을수없습니다."),
    INVALID_DURATION_WEEK(HttpStatus.BAD_REQUEST, "커리큘럼주차가 올바르지 않습니다."),
    COURSE_OWNER_CANNOT_LEAVE(HttpStatus.FORBIDDEN, "강의 개설자는 강의실을 나갈 수 없습니다."),
    FORBIDDEN_ASSIGNMENT_ACCESS(HttpStatus.FORBIDDEN, "강의 정책에 따라 이 과제에 접근할 수 없습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다.");


    private final HttpStatus status;
    private final String message;
}
