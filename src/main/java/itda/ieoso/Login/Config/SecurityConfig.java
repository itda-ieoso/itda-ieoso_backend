package itda.ieoso.Login.Config;

import itda.ieoso.Login.Jwt.JwtFilter;
import itda.ieoso.Login.Jwt.JwtUtil;
import itda.ieoso.Login.Jwt.LoginFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JwtUtil jwtUtil, @Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfigurationSource) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public static AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors((auth) -> auth.configurationSource(corsConfigurationSource));

        http
                .csrf((auth) -> auth.disable());

        http
                .formLogin((auth) -> auth.disable());

        http
                .httpBasic((auth) -> auth.disable());

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/users/sign-up", "/users/check-email", "/users/reset/password", "auth/**",
                                        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**","/webjars/**").permitAll()
                        .anyRequest().authenticated());


        http
                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // 현재 disable 되어있는 UsernamePasswordAuthenticationFilter 대신에 filter를 넣어줄 것이기 때문에 addFilterAt 사용
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);


        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .clearAuthentication(true)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        }));
        return http.build();
    }
}

