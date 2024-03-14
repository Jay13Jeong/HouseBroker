package com.jjeong.kiwi.aop;

import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NullCheckAspect {
    @Pointcut("@annotation(com.jjeong.kiwi.annotaion.NullCheck)")
    public void targetedEndpoints() {}

    @Around("targetedEndpoints()")
    public Object beforeMethodExecution(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        for (Object arg : joinPoint.getArgs()) {
            if (arg == null) {
                throw new IllegalArgumentException(method.getName());
            }
        }
        try {
            return joinPoint.proceed();
        } catch (Throwable t){
            return t;
        }
    }
}
