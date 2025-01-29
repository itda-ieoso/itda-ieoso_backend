package itda.ieoso.User;


import itda.ieoso.Response.BasicResponse;
import itda.ieoso.Response.DataResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import itda.ieoso.User.UserDTO.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/profile-image")
    public ResponseEntity<String> uploadProfileImage(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file) {

        String imageUrl = userService.uploadProfileImage(token, file);
        return ResponseEntity.ok(imageUrl);
    }

    @GetMapping("/profile-image")
    public ResponseEntity<String> getProfileImageUrl(
            @RequestHeader("Authorization") String token) {

        String imageUrl = userService.getProfileImageUrl(token);
        return ResponseEntity.ok(imageUrl);
    }

    // 모든 사용자 정보 조회
    @GetMapping
    public ResponseEntity<List<UserInfoDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUserInfo());
    }
}