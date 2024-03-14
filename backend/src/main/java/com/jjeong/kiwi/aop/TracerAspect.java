package com.jjeong.kiwi.aop;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class TracerAspect {
    private final Tracer tracer;
    @Pointcut("@annotation(com.jjeong.kiwi.annotaion.TraceEndpoint)")
    public void targetedEndpoints() {}

    @Around("targetedEndpoints()")
    public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        if (tracer == null) return joinPoint.proceed();

        String methodName = joinPoint.getSignature().toShortString();
        Span span = tracer.spanBuilder(methodName).setSpanKind(SpanKind.INTERNAL).startSpan();
//        Span span = tracer.spanBuilder(joinPoint.getSignature().toShortString()).startSpan();
        try {
            Object result = joinPoint.proceed();
            span.end();
            return result;
        } catch (Throwable throwable) {
            span.recordException(throwable);
            throw throwable;
        } finally {
            span.end();
        }
    }
}
