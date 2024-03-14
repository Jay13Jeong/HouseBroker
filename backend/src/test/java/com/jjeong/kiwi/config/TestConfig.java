package com.jjeong.kiwi.config;

import static org.mockito.Mockito.mock;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@TestConfiguration
public class TestConfig {
    @Bean
    @Primary //빈 우선등록.
    public JavaMailSender javaMailSender() {
        return mock(JavaMailSender.class);
    }

    @Bean
    @Primary
    public ClientRegistrationRepository clientRegistrationRepository() {
        return mock(ClientRegistrationRepository.class);
    }

    @Bean
    @Primary
    public OpenTelemetry openTelemetry() {
        return mock(OpenTelemetry.class);
    }

    @Bean
    @Primary
    public Tracer testTracer() {
        return mock(Tracer.class);
    }

}
