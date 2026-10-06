package project.camus.observability.common;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.opentelemetry.autoconfigure.OpenTelemetrySdkAutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * logback 로그를 opentelemetry 로 보내 OTLP 로 export 한다 (management.logging.export.otlp.enabled=true 일 때).
 * <p>
 * spring boot 는 OTLP 로그 exporter 만 구성하므로, logback 로그를 opentelemetry 로 넘기는 appender 를 여기서 설치한다.
 * traceId, spanId 는 현재 trace context 에서 자동으로 함께 기록된다.
 */
@AutoConfiguration(after = OpenTelemetrySdkAutoConfiguration.class)
@ConditionalOnClass({OpenTelemetryAppender.class, LoggerContext.class})
@ConditionalOnProperty(name = "management.logging.export.otlp.enabled", havingValue = "true")
public class OtlpLogAppenderAutoConfiguration {

    static final String APPENDER_NAME = "OTEL";

    @Bean
    @ConditionalOnBean(OpenTelemetry.class)
    public SmartInitializingSingleton otlpLogAppenderInstaller(OpenTelemetry openTelemetry) {

        return () -> {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger root = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
            if (root.getAppender(APPENDER_NAME) == null) {
                OpenTelemetryAppender appender = new OpenTelemetryAppender();
                appender.setName(APPENDER_NAME);
                appender.setContext(context);
                appender.setCaptureExperimentalAttributes(true);
                appender.start();
                root.addAppender(appender);
            }
            OpenTelemetryAppender.install(openTelemetry);
        };
    }
}
