package itda.ieoso.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import static itda.ieoso.User.UserRole.USER;

@Getter
@AllArgsConstructor
public class UserDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IdResponse {
        private Long userId;

        public static IdResponse of(User user) {
            return new IdResponse(user.getUserId());
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserRegistDto {
        @NotBlank(message = "이름을 입력해주세요.")
        private String name;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String password;

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "잘못된 이메일 형식입니다.")
        private String email;

        public User toUser(String encodedPassword) {
            return User.builder()
                    .name(name)
                    .password(encodedPassword)
                    .email(email)
                    .role(USER)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoDto {
        private Long userId;
        private String name;
        private String email;
        private String profileImageUrl;

        public static UserInfoDto of(User user, String profileImageUrl) {
            return UserInfoDto.builder()
                    .userId(user.getUserId())
                    .profileImageUrl(profileImageUrl)
                    .name(user.getName())
                    .email(user.getEmail())
                    .build();
        }
    }
}