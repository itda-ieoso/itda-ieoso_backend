package itda.ieoso.User;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

@Entity(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    private String email;

    private String password;

    private String name;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private String provider;

    private boolean service;  // 필수 약관 동의
    private boolean privacy;   // 필수 개인정보 동의
    private boolean marketing; // 선택 마케팅 동의

    // public으로 email 제공
    public String getEmail() {
        return email;
    }
    // public으로 password 제공
    public String getPassword() {
        return password;
    }

    // public으로 userId 제공
    public Long getUserId() { return userId; }

    @Builder
    public User(String name, String email, String password, UserRole role, boolean service, boolean privacy, boolean marketing) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.service = service;
        this.privacy = privacy;
        this.marketing = marketing;
    }

    @Builder
    public User(String oauthName, String oauthEmail, String provider) {
        this.name = oauthName;
        this.email = oauthEmail;
        this.role = UserRole.USER;
        this.service = true;
        this.privacy = true;
        this.marketing = true;
        this.provider = provider;
    }

    // S3에서 받은 URL 저장
    public void updateProfileImage(String imageUrl) {
        this.profileImageUrl = imageUrl;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void updateSocial(String oauthEmail, String oauthName, String provider) {
        this.email = oauthEmail;
        this.name = oauthName;
        this.provider = provider;

    }
}