package com.jjeong.kiwi.aop;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class JaegerAspect {

    @Autowired
    private Tracer tracer;

    @Around("execution(* com.jjeong.kiwi.controller.RealEstateController.*(..))")
    public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Span span = tracer.buildSpan(joinPoint.getSignature().getName()).start();
        try (Scope scope = tracer.scopeManager().activate(span)) {
            Object result = joinPoint.proceed();
            span.log("realEstateController-event");
//            span.setTag("http.method", "GET");
//            span.setTag("user.id", "12345");
            return result;
        } finally {
            span.finish();
        }
    }

    @Around("execution(* com.jjeong.kiwi.service.RealEstateService.*(..))")
    public Object traceMethod2(ProceedingJoinPoint joinPoint) throws Throwable {
        Span span = tracer.buildSpan(joinPoint.getSignature().getName()).start();
        try (Scope scope = tracer.scopeManager().activate(span)) {
            Object result = joinPoint.proceed();
            span.log("RealEstateService-event");
            return result;
        } finally {
            span.finish();
        }
    }

    @Around("execution(* com.jjeong.kiwi.repository.RealEstateRepository.*(..))")
    public Object traceMethod3(ProceedingJoinPoint joinPoint) throws Throwable {
        Span span = tracer.buildSpan(joinPoint.getSignature().getName()).start();
        try (Scope scope = tracer.scopeManager().activate(span)) {
            Object result = joinPoint.proceed();
            span.log("RealEstateRepository-event");
            return result;
        } finally {
            span.finish();
        }
    }
}

