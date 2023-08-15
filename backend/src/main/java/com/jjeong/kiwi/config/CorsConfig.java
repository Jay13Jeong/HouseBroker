package com.jjeong.kiwi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/socket.io/**")
                .allowedOrigins("http://localhost:3000","http://localhost:80") // 허용할 도메인
//                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
    }
}