package itda.ieoso.User;
import itda.ieoso.Exception.CustomException;
import itda.ieoso.Exception.ErrorCode;
import itda.ieoso.File.S3Service;
import itda.ieoso.Login.Jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import itda.ieoso.User.UserDTO.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final S3Service s3Service;

    // 이메일 중복 확인
    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    // 토큰으로 사용자 조회
    private User getUserByToken(String token) {
        String email = jwtUtil.getEmail(token.split(" ")[1]);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    // 회원 가입
    @Transactional
    public IdResponse signUp(UserRegistDto request) {
        if (isEmailDuplicate(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
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

    // 프로필 사진 업로드
    @Transactional
    public String uploadProfileImage(String token, MultipartFile file) {
        User user = getUserByToken(token);

        if (file.isEmpty()) {
            throw new CustomException(ErrorCode.EMPTY_FILE);
        }

        try {
            // 파일명: userId.jpg (고유한 ID 활용)
            String filename = "profile_" + user.getUserId() + ".jpg";

            // 로컬 임시 파일 생성
            File tempFile = File.createTempFile("upload-", filename);
            file.transferTo(tempFile);

            // S3에 파일 업로드
            String imageUrl = s3Service.uploadFile("profile_images", filename, tempFile);

            // DB에 프로필 이미지 URL 저장
            user.updateProfileImage(imageUrl);
            userRepository.save(user);

            // 임시 파일 삭제
            tempFile.delete();

            return imageUrl;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    // 프로필 사진 조회 (URL 반환)
    public String getProfileImageUrl(String token) {
        User user = getUserByToken(token);
        return user.getProfileImageUrl();
    }
}