package project.camus.feign.member.api.common;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import project.camus.webmvc.common.constants.CamusConstants;

@Slf4j
@Component
public class MemberWebFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest servletRequest = (HttpServletRequest) request;

        //TODO: delete
        String traceId = servletRequest.getHeader(CamusConstants.TRACE_ID);
        log.info(CamusConstants.TRACE_ID + ": " + traceId);

        chain.doFilter(request, response);
    }
}
