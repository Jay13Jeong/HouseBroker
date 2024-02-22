package com.jjeong.kiwi.aop;

import com.jjeong.kiwi.model.User;
import com.jjeong.kiwi.service.UserService;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAspect {

    private final UserService userService;
    private final static int allowLevel = Integer.parseInt(System.getenv("ADMIN_LEVEL"));
    private final static Logger logger = LoggerFactory.getLogger(AuthorizationAspect.class);

    @Pointcut("@annotation(com.jjeong.kiwi.annotaion.LoginCheck)")
    public void targetedEndpoints() {}

    @Pointcut("@annotation(com.jjeong.kiwi.annotaion.PermitCheck)")
    public void targetedEndpoints2() {}

    @Around("targetedEndpoints()")
    public Object aroundSecuredEndpoint(ProceedingJoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        try {
            long myId = userService.getIdByCookies(request.getCookies());
            User user = userService.getUserById(myId);
            if (user.isDormant()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.error("authorizationAspect", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        try {
            return joinPoint.proceed();
        } catch (Throwable t) {
            logger.error("aroundSecuredEndpoint", t);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @Around("targetedEndpoints2()")
    public Object aroundSecuredEndpoint2(ProceedingJoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        boolean authorized;

        try {
            authorized = userService.isAdminLevelUser(request.getCookies(), allowLevel);
        } catch (Exception e) {
            logger.error("authorizationAspect", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        try {
            if (!authorized) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
            return joinPoint.proceed();
        } catch (Throwable t) {
            logger.error("aroundSecuredEndpoint", t);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

}
