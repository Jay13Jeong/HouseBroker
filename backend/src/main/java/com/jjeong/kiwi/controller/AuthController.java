package com.jjeong.kiwi.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.repository.UserRepository;
import com.jjeong.kiwi.service.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;


//    @GetMapping("/google")
//    public RedirectView googleAuth() {
//        String googleAuthUrl = "redirect:/oauth2/authorization/google";
//        return new RedirectView(googleAuthUrl);
//    }

    @GetMapping("/google/login")
    public String googleAuth() {
        System.out.println("login start =======");
        return "google login api";
    }

    @GetMapping("/google/callback")
    public ResponseEntity<String> googleAuthCallback(@RequestParam("code") String authorizationCode, HttpServletResponse response) throws GeneralSecurityException, IOException {
        System.out.println("callback start =======");
        // Google OAuth 토큰 교환을 위한 인증 코드로 AccessToken 및 ID Token을 받아옵니다.
        GoogleTokenResponse tokenResponse = authService.exchangeAuthorizationCode(authorizationCode);

        // ID Token에서 사용자 정보를 읽어옵니다.
        GoogleIdToken idToken = authService.parseIdToken(tokenResponse.getIdToken());
        GoogleIdToken.Payload payload = idToken.getPayload();

        // 사용자 정보 추출
        String email = payload.getEmail();
        String userId = payload.getSubject();

        // 사용자 객체를 데이터베이스에 저장합니다. (예시로 UserRepository를 사용)
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setAuthid(userId);
            userRepository.save(user);
        }

        // 요청에 사용자 객체를 추가합니다.
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.setAttribute("user", user);
        System.out.println("callback end =======");
        return this.responseWithJWT(response, request);
    }

    private ResponseEntity<String> responseWithJWT(HttpServletResponse response, HttpServletRequest request) {
        // 사용자 인증 완료 후 처리할 로직을 작성합니다.
        // 사용자 정보는 필요에 따라 Principal 객체를 통해 사용할 수 있습니다.
        // 예: String username = principal.getName();

        // 로그인 로직 수행
        // ...



        // JWT 생성 및 설정
        String token = authService.generateToken( (User) request.getAttribute("user") );
        response.addHeader("Authorization", "Bearer " + token);

        // 리다이렉트 설정
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", "http://" + System.getenv("SERVER_HOST") + "/api");

        return ResponseEntity.ok("Google authentication successful.");
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        response.setHeader("Set-Cookie", "jwt=; Path=/; Max-Age=0; HttpOnly"); // JWT 쿠키 삭제
        return ResponseEntity.ok("Logout successful.");
    }
}