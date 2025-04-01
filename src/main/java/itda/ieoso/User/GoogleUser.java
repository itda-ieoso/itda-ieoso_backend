package itda.ieoso.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GoogleUser {
    private String sub;
    private String name;
    private String givenName;
    private String familyName;
    private String picture;
    private String email;
    private Boolean verifiedEmail;
    private String locale;
}
