package itda.ieoso.Login.Jwt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {
    @Id
    private String email;

    @Column(nullable = false, length = 1000)
    private String token;

    public void updateToken(String newToken) {
        this.token = newToken;
    }

    public RefreshToken(String email, String token) {
        this.email = email;
        this.token = token;
    }


}
