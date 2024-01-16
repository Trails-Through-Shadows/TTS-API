package cz.trailsthroughshadows.api.configuration;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RequestResponseLogging implements Filter {
    private final String split = "-".repeat(50);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        long startTime = System.currentTimeMillis();

        log.info(split);

        String method = ((HttpServletRequest) request).getMethod();
        String path = ((HttpServletRequest) request).getRequestURI();
        String queryString = ((HttpServletRequest) request).getQueryString();

        if (queryString == null) {
            log.info("New Request | {} {}", method, path);
        } else {
            log.info("New Request | {} {}?{}", method, path, queryString);
        }

        chain.doFilter(request, response);

        log.info("Request Duration: {}ms", System.currentTimeMillis() - startTime);
    }
}