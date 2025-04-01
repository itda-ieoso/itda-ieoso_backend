package itda.ieoso.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itda.ieoso.Exception.CustomException;
import itda.ieoso.Exception.ErrorCode;
import itda.ieoso.Login.Jwt.JwtUtil;
import itda.ieoso.Response.Response;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService {

    private final ObjectMapper jacksonObjectMapper;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${client_id}")
    private String clientId;

    @Value("${client_secret}")
    private String clientSecret;

    @Value("${redirect_uri}")
    private String redirectUri;

    @Value("${redirect_uri_temp}")
    private String redirectUriTemp;

    private String response_type = "code";

    @Value("${scope}")
    private String scope;

    private final HttpServletResponse response;

    // 업데이트 이후 신규가입자용 api
    public void googleRedirectURL() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("scope", scope);
        params.put("response_type", response_type);
        params.put("client_id", clientId);
        params.put("redirect_uri", redirectUri);

        String parameterString = params.entrySet().stream()
                        .map(x->x.getKey()+"="+x.getValue())
                                .collect(Collectors.joining("&"));
        String redirectUrl = "https://accounts.google.com/o/oauth2/v2/auth"+"?"+parameterString;
        response.sendRedirect(redirectUrl);
    }

    // 업데이트 이후 신규가입자용 api
    public ResponseEntity<Map<String, String>> googleLogin(String code) throws JsonProcessingException {
        System.out.println("code = " + code);
        // oauth 로그인 엑세스토큰 발급
        ResponseEntity<String> accessToken = requestAccessToken(code);
        GoogleOAuthToken oAuthToken = getAccessToken(accessToken);

        // oauth user정보 가져오기
        ResponseEntity<String> userInfoResponse = requestUserInfo(oAuthToken);
        System.out.println("userInfoResponse = " + userInfoResponse.getBody());
        GoogleUser googleUser = getUserInfo(userInfoResponse);
        if (googleUser == null) {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }

        // 기존 로그인 기록이 존재하는지 검증
        Boolean userexist = userRepository.existsByEmail(googleUser.getEmail());
        // 계정 정보가 없는경우
        if (!userexist) {
            // google login 정보 저장
            User user = new User(googleUser.getName(), googleUser.getEmail());
            userRepository.save(user);
            log.info("신규유저 구글로그인 정보 저장 완료");
        }

        // jwt토큰 생성
        String email = googleUser.getEmail();
        String jwtToken = jwtUtil.createJwt(email, "USER", 60 * 60 * 10L);
        log.info("jwt 토큰 생성");

        // 헤더에 jwt토큰 넣기
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        // 반환
        // GoogleLoginResponse googleLoginResponse = new GoogleLoginResponse(jwtToken, 1, oAuthToken.getAccess_token(), oAuthToken.getToken_type());
        //return new ResponseEntity<>(googleLoginResponse, headers, HttpStatus.OK);

        //return new ResponseEntity<>("구글 로그인 성공", headers, HttpStatus.OK);

        Map<String, String> response = new HashMap<>();
        response.put("jwtToken", jwtToken); // 발급한 JWT 토큰

        return ResponseEntity.ok(response); // 클라이언트에게 리다이렉트 URL과 JWT를 반환
    }


    // 기존 유저 소셜로그인 연동 api
    public void googleRedirectURLTemp() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("scope", scope);
        params.put("response_type", response_type);
        params.put("client_id", clientId);
        params.put("redirect_uri", redirectUriTemp);

        String parameterString = params.entrySet().stream()
                .map(x->x.getKey()+"="+x.getValue())
                .collect(Collectors.joining("&"));
        String redirectUrl = "https://accounts.google.com/o/oauth2/v2/auth"+"?"+parameterString;
        response.sendRedirect(redirectUrl);

    }

    // 기존 유저 소셜로그인 연동 api
    public ResponseEntity<Map<String, String>> googleLoginTemp(String code) throws JsonProcessingException {
        // oauth 로그인 엑세스토큰 발급
        ResponseEntity<String> accessToken = requestAccessToken(code);
        GoogleOAuthToken oAuthToken = getAccessToken(accessToken);

        // oauth user정보 가져오기
        ResponseEntity<String> userInfoResponse = requestUserInfo(oAuthToken);
        GoogleUser googleUser = getUserInfo(userInfoResponse);
        if (googleUser == null) {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }

        // 기존계정 찾기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }
        String localemail = authentication.getName();
        User user = userRepository.findByEmail(localemail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // oauth user정보 저장
        user.updateSocial(googleUser.getName(), googleUser.getEmail());
        // 이후에는 소셜로그인으로만 접근가능
        user.setPassword(null);
        userRepository.save(user);
        log.info("기존계정 - 소셜로그인 연동 완료");


        // jwt토큰 생성
        String email = googleUser.getEmail();
        String jwtToken = jwtUtil.createJwt(email, "USER", 60 * 60 * 10L);

        // 헤더에 jwt토큰 넣기
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        // 반환
        //GoogleLoginResponse googleLoginResponse = new GoogleLoginResponse(jwtToken, 1, oAuthToken.getAccess_token(), oAuthToken.getToken_type());
        //return new ResponseEntity<>(googleLoginResponse, headers, HttpStatus.OK);

        Map<String, String> response = new HashMap<>();
        response.put("jwtToken", jwtToken); // 발급한 JWT 토큰

        return ResponseEntity.ok(response); //

    }

    public ResponseEntity<String> requestAccessToken(String code) {
        String GOOGLE_ACCESS_TOKEN_URL = "https://oauth2.googleapis.com/token";
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("code", code);
        params.put("redirect_uri", redirectUri);
        params.put("grant_type", "authorization_code");

        ResponseEntity<String> responseEntity= restTemplate.postForEntity(GOOGLE_ACCESS_TOKEN_URL, params, String.class);

        if (responseEntity.getStatusCode()== HttpStatus.OK) {
            return responseEntity;
        }

        return null;
    }

    public GoogleOAuthToken getAccessToken(ResponseEntity<String> response) throws JsonProcessingException {
        GoogleOAuthToken googleOAuthToken = jacksonObjectMapper.readValue(response.getBody(), GoogleOAuthToken.class);
        return googleOAuthToken;

    }

    public ResponseEntity<String> requestUserInfo(GoogleOAuthToken oAuthToken) {

        String GOOGLE_USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " +oAuthToken.getAccess_token());


        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(GOOGLE_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
        return response;

    }

    public GoogleUser getUserInfo(ResponseEntity<String> userInfoResponse) throws JsonProcessingException {
        GoogleUser googleUser = jacksonObjectMapper.readValue(userInfoResponse.getBody(), GoogleUser.class);
        return googleUser;
    }

}