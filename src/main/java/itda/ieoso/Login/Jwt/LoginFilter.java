package itda.ieoso.Login.Jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import itda.ieoso.Login.Dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    CachedBodyHttpServletRequest cachedBodyHttpServletRequest;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter("email");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String email = null;
        String password = null;

        try {
            cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(request);
            if ("application/json".equals(cachedBodyHttpServletRequest.getContentType())) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> jsonRequest = objectMapper.readValue(cachedBodyHttpServletRequest.getInputStream(),
                        new TypeReference<Map<String, String>>() {});
                email = jsonRequest.get("email");
                password = jsonRequest.get("password");

                System.out.println("email: " + email);
                System.out.println("password: " + password);
            }else {
                email = obtainUsername(cachedBodyHttpServletRequest);
                password = obtainPassword(cachedBodyHttpServletRequest);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }




        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);
        System.out.println("loginfilter");
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("login success");
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        String token = jwtUtil.createJwt(username, 60 * 60 * 10L);

        System.out.println(token);
        response.addHeader("Authorization", "Bearer " + token);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {


        BufferedReader reader = cachedBodyHttpServletRequest.getReader();
        String requestBody = reader.lines().collect(Collectors.joining(System.lineSeparator()));

        System.out.println("login failed" + failed.getMessage());
        System.out.println("Request URI: " + cachedBodyHttpServletRequest.getRequestURI());
        System.out.println("Request Method: " + cachedBodyHttpServletRequest.getMethod());

        System.out.println("Request Headers: " + Collections.list(cachedBodyHttpServletRequest.getHeaderNames()).stream()
                .collect(Collectors.toMap(h -> h, cachedBodyHttpServletRequest::getHeader)));

        System.out.println("Request Body: " + requestBody);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + failed.getMessage() + "\"}");

        System.out.println("Response Status: " + response.getStatus());
        System.out.println("Response Content-Type: " + response.getContentType());


    }
}
