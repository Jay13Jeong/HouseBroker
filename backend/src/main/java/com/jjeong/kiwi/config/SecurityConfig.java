package com.jjeong.kiwi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/api,/api/auth/google/login,/api/auth/google/callback,/api/oauth2/authorization/google").permitAll()
                .antMatchers("/api/**,/api/,/").permitAll()
//                .anyRequest().authenticated()
                .and()
//                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .oauth2Login()
//                .loginPage("/oauth2/authorization/google")
                .defaultSuccessUrl("/api/auth/google/callback")
                .and()
                .logout()
                .logoutSuccessUrl("/api")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
    }
}
