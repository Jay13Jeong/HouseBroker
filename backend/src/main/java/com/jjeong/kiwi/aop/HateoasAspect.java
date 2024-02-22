package com.jjeong.kiwi.aop;

import com.jjeong.kiwi.model.RealEstate;
import com.sun.corba.se.spi.ior.Identifiable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class HateoasAspect {

        @Pointcut("@annotation(com.jjeong.kiwi.annotaion.Hateoasify)")
        public void hateoasAnnotatedMethod() {
        }

}
