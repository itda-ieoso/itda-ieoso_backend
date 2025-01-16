package itda.ieoso.User.Service;
import itda.ieoso.Login.Jwt.JwtUtil;
import itda.ieoso.User.Domain.User;
import itda.ieoso.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import itda.ieoso.User.Dto.UserDto.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    // 이메일 중복 확인
    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    // 토큰으로 사용자 조회
    private User getUserByToken(String token) {
        String email = jwtUtil.getEmail(token.split(" ")[1]);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 조회할 수 없습니다."));
    }

    // 회원 가입
    @Transactional
    public IdResponse signUp(UserRegistDto request) {
        if (isEmailDuplicate(request.getEmail())) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }

        User user = request.toUser(bCryptPasswordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return IdResponse.of(user);
    }

    // 계정 삭제
    @Transactional
    public void deleteAccount(String token) {
        User user = getUserByToken(token);
        userRepository.delete(user);
    }

    // 회원 정보 조회
    @Transactional(readOnly = true)
    public UserInfoDto getUserInfo(String token) {
        User user = getUserByToken(token);
        String imageUrl = null;
        return UserInfoDto.of(user, imageUrl);
    }

    // 모든 사용자 정보 조회
    @Transactional(readOnly = true)
    public List<UserInfoDto> getAllUserInfo() {
        List<UserInfoDto> users = new ArrayList<>();
        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            users.add(UserInfoDto.of(user, null));
        }

        return users;
    }
}