package itda.ieoso.User.Controller;


import itda.ieoso.Response.BasicResponse;
import itda.ieoso.Response.DataResponse;
import itda.ieoso.User.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import itda.ieoso.User.Dto.UserDto.*;

import java.util.List;

@RestController
@RequestMapping("/users") // 공통 경로 추가
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 가입
    @PostMapping(path = "/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<? extends BasicResponse> signUp(
            @Valid @RequestBody UserRegistDto request
    ) {
        return ResponseEntity.ok().body(
                new DataResponse<>(userService.signUp(request))
        );
    }

    // 이메일 중복 확인
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailDuplicate(@RequestParam(value = "email") String email) {
        return ResponseEntity.ok(userService.isEmailDuplicate(email));
    }

    // 계정 삭제
    @DeleteMapping("/delete-account")
    public ResponseEntity<String> deleteAccount(@RequestHeader("Authorization") String token) {
        userService.deleteAccount(token);
        return ResponseEntity.ok("계정 삭제 완료");
    }

    // 회원 정보 조회
    @GetMapping("/user-info")
    public ResponseEntity<UserInfoDto> getUserInfo(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(userService.getUserInfo(token));
    }

    // 모든 사용자 정보 조회
    @GetMapping
    public ResponseEntity<List<UserInfoDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUserInfo());
    }
}