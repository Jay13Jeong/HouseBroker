package com.jjeong.kiwi.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class HateoasAspect {

        @Pointcut("@annotation(com.jjeong.kiwi.annotaion.Hateoasify)")
        public void hateoasAnnotatedMethod() {
        }

}
