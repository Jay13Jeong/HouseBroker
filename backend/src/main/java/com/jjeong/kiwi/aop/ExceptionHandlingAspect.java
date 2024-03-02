package com.jjeong.kiwi.aop;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionHandlingAspect {
    @Pointcut("@annotation(com.jjeong.kiwi.annotaion.ExceptionHandling)")
    public void targetedEndpoints() {}
    @AfterThrowing(pointcut = "targetedEndpoints()", throwing = "exception")
    public ResponseEntity<Object> handleException(Exception exception) {
        if (exception.getMessage().contains("400"))
            return new ResponseEntity<>("Bad Req Error", HttpStatus.BAD_REQUEST);
        if (exception.getMessage().contains("403"))
            return new ResponseEntity<>("Forbidden Error", HttpStatus.FORBIDDEN);
        if (exception.getMessage().contains("404"))
            return new ResponseEntity<>("Not Found Error", HttpStatus.NOT_FOUND);
        if (exception.getMessage().contains("409"))
            return new ResponseEntity<>("Conflict Error", HttpStatus.CONFLICT);
        return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
