package com.jjeong.kiwi.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.repository.UserRepository;
import com.jjeong.kiwi.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    private static final String SECRET_KEY = System.getenv("JWTKEY");

    @GetMapping("/google/login")
    public RedirectView googleAuth(HttpServletResponse response) {
        System.out.println("login start =======/");
//        response.setStatus(HttpServletResponse.SC_FOUND);
//        response.addHeader("Location", "http://" + System.getenv("SERVER_HOST") + "/api/oauth2/authorization/google");
//        System.out.println(response);
        return new RedirectView("/api/oauth2/authorization/google");
    }

    @GetMapping("/google/callback")
    public ResponseEntity<String> googleAuthCallback(@RequestParam("code") String authorizationCode, HttpServletResponse response) {
//        System.out.println("callback start =======");

        User userInfo = authService.getInfoByCode(authorizationCode);
        String email = userInfo.getEmail();
        String authId = userInfo.getAuthid();

//        System.out.println("callback part 1 =======");

        // 사용자 객체를 데이터베이스에 저장합니다. (예시로 UserRepository를 사용)
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setAuthid(authId);
            user.setUsername(userInfo.getUsername());
            userRepository.save(user);
        }

//        System.out.println("callback part 2 =======");
//        System.out.println(user.toString());

        // 요청에 사용자 객체를 추가합니다.
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.setAttribute("user", user);
//        System.out.println("callback end =======");
        return this.responseWithJWT(response, request);
    }

    private ResponseEntity<String> responseWithJWT(HttpServletResponse response, HttpServletRequest request) {

        // JWT 생성 및 설정
        String token = authService.generateToken( (User) request.getAttribute("user") );
        System.out.println("responseWithJWT user =======");
        System.out.println((User) request.getAttribute("user"));
        System.out.println(token);
//        System.out.println(authService.extractSubject(token));
        System.out.println("responseWithJWT end =======");

        // Create a JWT cookie
        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60); // Set the cookie's expiration time (e.g., 24 hours)
        jwtCookie.setSecure(true); // Set whether the cookie should only be sent over HTTPS
        jwtCookie.setHttpOnly(true); // Set whether the cookie should be accessible only through HTTP or HTTPS

        // Add the JWT cookie to the response
        response.addCookie(jwtCookie);

        // 리다이렉트 설정
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", "http://" + System.getenv("SERVER_HOST") + "/");

        return ResponseEntity.ok("Google authentication successful.");
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        response.setHeader("Set-Cookie", "jwt=; Path=/; Max-Age=0; HttpOnly"); // JWT 쿠키 삭제
        return ResponseEntity.ok("Logout successful.");
    }
}