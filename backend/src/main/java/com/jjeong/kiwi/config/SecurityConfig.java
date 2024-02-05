package com.jjeong.kiwi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors() // Enable CORS
//                .and()
//                .logout()
//                .logoutUrl("/delete/jsession") // post로 접근필요.
//                .logoutSuccessUrl("/")
//                .invalidateHttpSession(true)
//                .clearAuthentication(true) // 인증정보 삭제.
//                .deleteCookies("JSESSIONID")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .oauth2Login()
                .defaultSuccessUrl("/delete/jsession")
                .and()
                .csrf().disable();
    }

}
