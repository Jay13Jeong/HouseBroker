package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.dto.SignupRequest;
import com.jjeong.kiwi.model.User;
import com.jjeong.kiwi.service.AuthService;
import com.jjeong.kiwi.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.view.RedirectView;

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
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.addHeader("Location", "/api/oauth2/authorization/google");
    }

    @GetMapping("/google/callback")
    public void googleAuthCallback(@RequestParam("code") String authorizationCode, HttpServletResponse response) {

        User userInfo = authService.getInfoByCode(authorizationCode);
        String email = userInfo.getEmail();

        User user = userService.getUserByEmail(email);

        if (user == null) {
            SignupRequest signupRequest = new SignupRequest();
            signupRequest.setEmail(email);
            signupRequest.setUsername(userInfo.getUsername());
            userService.createUser(signupRequest, false);
            user = userService.getUserByEmail(email);
        }

        // 요청에 사용자 객체를 추가합니다.
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.setAttribute("user", user);
        authService.responseWithJWT(response, request);
    }

    @GetMapping("/logout")
    public RedirectView logout(HttpServletResponse response) {
        response.setHeader("Set-Cookie", "jwt=; Path=/; Max-Age=0; HttpOnly"); // JWT 쿠키 삭제
        return new RedirectView("/");
    }

    @PostMapping("/login")
    public void signIn(@RequestBody SignupRequest signupRequest, HttpServletResponse response) {
        User user = userService.getUserByEmailAndPwd(signupRequest);
        if (user == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.setAttribute("user", user);
        authService.responseWithJWT(response, request);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @PostMapping("/email/code")
    public void sendConfirmMail(@RequestBody SignupRequest signupRequest, HttpServletResponse response) {
        String email = signupRequest.getEmail();
        if (!authService.emailValidate(email)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (userService.getUserByEmail(email) != null){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        }
        if (!authService.sendConfirmMail(email)){
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }


}