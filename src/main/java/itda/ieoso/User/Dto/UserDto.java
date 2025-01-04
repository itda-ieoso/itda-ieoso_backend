package itda.ieoso.User.Dto;

import itda.ieoso.User.Domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IdResponse {
        private String userId;

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

        private String fileName;

        public User toUser(String encodedPassword) {
            return User.builder()
                    .name(name)
                    .password(encodedPassword)
                    .email(email)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoDto {
        private String userId;
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