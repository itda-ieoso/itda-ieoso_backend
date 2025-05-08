package itda.ieoso.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import itda.ieoso.Response.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/oauth") // 공통 경로 추가
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    // 신규 고객용 소셜로그인
    @GetMapping("/google/login")
    public void googlelogin() throws IOException {
        oAuthService.googleRedirectURL();
    }

    // 신규고객용 소셜로그인 리다이렉트
    @GetMapping("/return/uri")
    public ResponseEntity<Map<String, String>> returnUri(@RequestParam String code) throws JsonProcessingException {
        return oAuthService.googleLogin(code);
    }

    // 기존고객용 소셜로그인 연동
    @GetMapping("/google/login/temp")
    public void googleloginTemp() throws IOException {
        oAuthService.googleRedirectURLTemp();
    }

    // 기존고객용 소셜로그인 연동 리다이렉트
    @GetMapping("/return/uri/temp")
    public ResponseEntity<Map<String, String>> returnUriTemp(@RequestParam String code) throws JsonProcessingException {
        return oAuthService.googleLoginTemp(code);
    }

    // 소셜로그인 연동 여부
    @GetMapping("/social/linked")
    public Response<String> getProvider() {
        return Response.success("소셜로그인 연동 유무", oAuthService.getProvider());
    }

    @PostMapping("/reissuetoken")
    public ResponseEntity<Map<String, String>> reissueAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return oAuthService.reissueAccessToken(refreshToken);
    }
}
