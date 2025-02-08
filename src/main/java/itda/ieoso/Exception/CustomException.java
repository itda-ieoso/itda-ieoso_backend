package itda.ieoso.Exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ErrorCode의 값을 매개변수로 생성자 호출 필요
 */
@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

}
