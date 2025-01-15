package itda.ieoso.User.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    // 헬스 체크 엔드포인트
    @GetMapping("/health")
    public String healthCheck() {
        return "서버가 실행 중입니다!";
    }
}
