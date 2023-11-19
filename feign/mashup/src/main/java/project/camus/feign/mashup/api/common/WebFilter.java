package project.camus.feign.mashup.api.common;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import project.camus.webmvc.common.constants.CamusConstants;

@Slf4j
@Component
public class WebFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String traceId = UUID.randomUUID().toString();
        log.info("traceId : " + traceId);
        Objects.requireNonNull(RequestContextHolder.getRequestAttributes())
            .setAttribute(CamusConstants.TRACE_ID, traceId, RequestAttributes.SCOPE_REQUEST);
        chain.doFilter(request, response);
    }
}
