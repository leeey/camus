package project.camus.observability.common

import ch.qos.logback.classic.LoggerContext
import io.opentelemetry.api.OpenTelemetry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import spock.lang.Specification

class OtlpLogAppenderAutoConfigurationTest extends Specification {

    def runner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(OtlpLogAppenderAutoConfiguration))
        .withBean(OpenTelemetry, { OpenTelemetry.noop() })

    def root = (LoggerFactory.getILoggerFactory() as LoggerContext).getLogger(Logger.ROOT_LOGGER_NAME)

    def cleanup() {

        root.detachAppender(OtlpLogAppenderAutoConfiguration.APPENDER_NAME)
    }

    def "installs the opentelemetry appender when otlp log export is enabled"() {

        expect:
        runner.withPropertyValues("management.logging.export.otlp.enabled=true").run { context ->
            assert context.containsBean("otlpLogAppenderInstaller")
            assert root.getAppender(OtlpLogAppenderAutoConfiguration.APPENDER_NAME) != null
        }
    }

    def "does nothing when otlp log export is disabled"() {

        expect:
        runner.run { context ->
            assert !context.containsBean("otlpLogAppenderInstaller")
            assert root.getAppender(OtlpLogAppenderAutoConfiguration.APPENDER_NAME) == null
        }
    }
}
