package com.jjeong.kiwi.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.ResourceAttributes;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    private static final Logger logger = LoggerFactory.getLogger(OpenTelemetryConfig.class);
    private final String serviceName = "otel-jaeger-custom";
    @Bean
    OpenTelemetry initOpenTelemetry() {
        try {
            String endpoint = System.getenv("OTEL_EXPORTER_OTLP_ENDPOINT");
            // Export traces to Jaeger over OTLP
            OtlpGrpcSpanExporter jaegerOtlpExporter =
                OtlpGrpcSpanExporter.builder()
                    .setEndpoint(endpoint)
                    .setTimeout(30, TimeUnit.SECONDS)
                    .build();

            Resource serviceNameResource =
                Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, serviceName));

            // Set to process the spans by the Jaeger Exporter
            SdkTracerProvider tracerProvider =
                SdkTracerProvider.builder()
                    .addSpanProcessor(BatchSpanProcessor.builder(jaegerOtlpExporter).build())
                    .setResource(Resource.getDefault().merge(serviceNameResource))
                    .build();
            OpenTelemetrySdk openTelemetry =
                OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).build();

            // it's always a good idea to shut down the SDK cleanly at JVM exit.
            Runtime.getRuntime().addShutdownHook(new Thread(tracerProvider::close));

            return openTelemetry;
        } catch (Exception e){
            logger.error("OpenTelemetryConfig:initOpenTelemetry", e);
            return null;
        }
    }

    @Bean
    Tracer tracer(OpenTelemetry openTelemetry) {
        if (openTelemetry != null) {
            return openTelemetry.getTracer(serviceName);
        } else {
            return null;
        }
    }
}
