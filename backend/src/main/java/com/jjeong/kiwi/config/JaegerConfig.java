package com.jjeong.kiwi.config;

import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JaegerConfig {
    @Bean
    public io.opentracing.Tracer jaegerTracer() {
        return new io.jaegertracing.Configuration("spring-boot-instance-1")
            .withReporter(new ReporterConfiguration().withLogSpans(true)) // 로깅 활성화.
            .withSampler(new SamplerConfiguration().withType("const").withParam(1)) // 100% 샘플링.
            .getTracer();
    }

}
