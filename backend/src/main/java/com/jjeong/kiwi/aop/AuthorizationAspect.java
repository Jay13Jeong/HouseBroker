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
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAspect {

    private final UserService userService;
    private final static Logger logger = LoggerFactory.getLogger(AuthorizationAspect.class);
    private static int allowLevel = 10;
    static {
        String initAllowLevel = System.getenv("ADMIN_LEVEL");
        if (initAllowLevel != null && !initAllowLevel.isEmpty()){
            allowLevel = Integer.parseInt(initAllowLevel);
        }
    }

    @Pointcut("@annotation(com.jjeong.kiwi.annotaion.LoginCheck)")
    public void targetedEndpoints() {}

    @Pointcut("@annotation(com.jjeong.kiwi.annotaion.PermitCheck)")
    public void targetedEndpoints2() {}

    @Around("targetedEndpoints()")
    public Object aroundSecuredEndpoint(ProceedingJoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        try {
            User user = userService.getUserByCookies(request.getCookies());
            if (user.isDormant()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.error("authorizationAspectException", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        try {
            return joinPoint.proceed();
        } catch (ResponseStatusException rse) {
            logger.error("aroundSecuredEndpoint:rse:", rse);
            throw rse;
        } catch (Throwable t) {
            logger.error("aroundSecuredEndpointThrowable", t);
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
            logger.error("aroundSecuredEndpoint2Exception", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        try {
            if (!authorized) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
            return joinPoint.proceed();
        } catch (ResponseStatusException rse) {
            logger.error("aroundSecuredEndpoint2:rse:", rse);
            throw rse;
        } catch (Throwable t) {
            logger.error("aroundSecuredEndpoint2Throwable", t);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

}
