package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.domain.SignupRequest;
import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.service.AuthService;
import com.jjeong.kiwi.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    private static final String SECRET_KEY = System.getenv("JWTKEY");

    @GetMapping("/google/login")
    public void googleAuth(HttpServletResponse response) {
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
        User user = userService.getUserByEmail(email);

        if (user == null) {
            SignupRequest signupRequest = new SignupRequest();
            signupRequest.setEmail(email);
            signupRequest.setUsername(userInfo.getUsername());
            userService.createUser(signupRequest, false);
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

    @PostMapping("/login")
    public void signIn(@RequestBody SignupRequest signupRequest, HttpServletResponse response) {
        User user = userService.getUserByEmailAndPwd(signupRequest);
        if (user == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.setAttribute("user", user);
        this.responseWithJWT(response, request);
        response.setStatus(HttpServletResponse.SC_OK);
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
        response.setHeader("Location", "/auth/callback");
    }

    @GetMapping("/logout")
    public RedirectView logout(HttpServletResponse response) {
        response.setHeader("Set-Cookie", "jwt=; Path=/; Max-Age=0; HttpOnly"); // JWT 쿠키 삭제
        return new RedirectView("/");
    }

    @PostMapping("/email/code")
    public void sendConfirmMail(@RequestBody SignupRequest signupRequest, HttpServletResponse response) {
        String email = signupRequest.getEmail();
        if (!emailValidate(email)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (!authService.sendConfirmMail(email)){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private static boolean emailValidate(final String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}