package itda.ieoso.Login.Jwt;

import itda.ieoso.User.Domain.User;
import itda.ieoso.Login.Dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        System.out.println("Authorization Header: " + authorization);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            System.out.println("token null");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];

        if (jwtUtil.isExpired(token)) {
            System.out.println("token expired");
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtUtil.getEmail(token);

        User user = User.builder()
                .email(email)
                .password("temp")  // We don't need the password here for authentication
                .name("tempName") // 이름도 임시로 설정
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        // Create an authentication token for Spring Security
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // Set the authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
