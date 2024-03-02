package com.jjeong.kiwi.aop;

import javax.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class HeaderAspect {
    @Pointcut("@annotation(com.jjeong.kiwi.annotaion.CommonResponseHeader)")
    public void targetedEndpoints() {}

    @Before("targetedEndpoints()")
    public void addCustomHeader(JoinPoint joinPoint) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        // 공통 헤더 추가 로직
//        response.setHeader("Expires", "Thu, 01 Jan 1970 00:00:00 GMT"); // epoch 설정.
        response.setHeader("Expires", "0"); // epoch 설정.
    }

}
