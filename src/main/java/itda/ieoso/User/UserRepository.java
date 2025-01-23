package itda.ieoso.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // 이메일로 사용자 검색

    boolean existsByEmail(String email); // 이메일 중복 여부 확인
}

