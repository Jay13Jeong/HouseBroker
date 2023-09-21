package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.domain.SignupRequest;
import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.service.AuthService;
import com.jjeong.kiwi.service.UserService;
import lombok.RequiredArgsConstructor;

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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    private static final String SECRET_KEY = System.getenv("JWTKEY");

    @GetMapping("/google/login")
    public void googleAuth(HttpServletResponse response) {
        System.out.println("login start =======/");
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.addHeader("Location", "/api/oauth2/authorization/google");
//        System.out.println(response);
    }

    @GetMapping("/google/callback")
    public void googleAuthCallback(@RequestParam("code") String authorizationCode, HttpServletResponse response) {
//        System.out.println("callback start =======");

        User userInfo = authService.getInfoByCode(authorizationCode);
        String email = userInfo.getEmail();
        String authId = userInfo.getAuthid();

//        System.out.println("callback part 1 =======");

        // 사용자 객체를 데이터베이스에 저장합니다. (예시로 UserRepository를 사용)
        User user = userService.getUserByEmail(email);

        if (user == null) {
            SignupRequest signupRequest = new SignupRequest();
            signupRequest.setEmail(email);
            signupRequest.setUsername(userInfo.getUsername());
            userService.createUser(signupRequest);
            user = userService.getUserByEmail(email);
//          user.setAuthid(authId);
        }

//        System.out.println("callback part 2 =======");
//        System.out.println(user.toString());

        // 요청에 사용자 객체를 추가합니다.
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.setAttribute("user", user);
//        System.out.println("callback end =======");
        this.responseWithJWT(response, request);
    }

    private void responseWithJWT(HttpServletResponse response, HttpServletRequest request) {

        // JWT 생성 및 설정
        String token = authService.generateToken( (User) request.getAttribute("user") );

        // Create a JWT cookie
        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60); // 쿠키 만료시간
        jwtCookie.setSecure(true);
        jwtCookie.setHttpOnly(true);

        // Add the JWT cookie to the response
        response.addCookie(jwtCookie);

        // 리다이렉트 설정
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", "/");
    }

    @GetMapping("/logout")
    public RedirectView logout(HttpServletResponse response) {
        response.setHeader("Set-Cookie", "jwt=; Path=/; Max-Age=0; HttpOnly"); // JWT 쿠키 삭제
        return new RedirectView("/");
    }
}