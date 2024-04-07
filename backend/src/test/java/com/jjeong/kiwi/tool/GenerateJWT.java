package com.jjeong.kiwi.tool;

import com.jjeong.kiwi.service.AuthService;
import javax.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GenerateJWT {

    @Autowired
    private AuthService authService;

    @Bean
    public Cookie generateJWT_forTest() {
        // JWT 생성 및 설정
        String token = authService.generateToken(SampleData.getUser());
        // JWT cookie
        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60); // 쿠키 만료시간
        jwtCookie.setSecure(true);
        jwtCookie.setHttpOnly(true);
        return jwtCookie;
    }
}
