package itda.ieoso.User;


import itda.ieoso.Response.BasicResponse;
import itda.ieoso.Response.DataResponse;
import itda.ieoso.Response.Response;
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
    public Response<IdResponse> signUp(
            @Valid @RequestBody UserRegistDto request
    ) {
        return Response.success("회원가입 성공", userService.signUp(request));
    }

    // 이메일 중복 확인
    @GetMapping("/check-email")
    public Response<Boolean> checkEmailDuplicate(@RequestParam(value = "email") String email) {
        return Response.success("이메일 중복 여부", userService.isEmailDuplicate(email));
    }

    // 계정 삭제
    @DeleteMapping("/delete-account")
    public Response<?> deleteAccount(@RequestHeader("Authorization") String token) {
        userService.deleteAccount(token);
        return Response.success("계정 삭제 완료", null);
    }

    // 회원 정보 조회
    @GetMapping("/user-info")
    public Response<UserInfoDto> getUserInfo(@RequestHeader("Authorization") String token) {
        return Response.success("유저 정보 반환", userService.getUserInfo(token));
    }

    @PostMapping("/profile-image")
    public Response<String> uploadProfileImage(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file) {

        String imageUrl = userService.uploadProfileImage(token, file);
        return Response.success("프로필 사진 변경", imageUrl);
    }

    @GetMapping("/profile-image")
    public Response<String> getProfileImageUrl(
            @RequestHeader("Authorization") String token) {

        String imageUrl = userService.getProfileImageUrl(token);
        return Response.success("프로필 사진 조회", imageUrl);
    }

    // 모든 사용자 정보 조회
    @GetMapping
    public Response<List<UserInfoDto>> getAllUsers() {
        return Response.success("모든 사용자 정보 조회", userService.getAllUserInfo());
    }
}