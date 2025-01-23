package itda.ieoso.User;

import lombok.*;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    private final String authority;
}


