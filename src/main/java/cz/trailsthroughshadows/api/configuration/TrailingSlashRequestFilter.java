package cz.trailsthroughshadows.api.configuration;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class TrailingSlashRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        if (requestURI.endsWith("/")) {
            log.debug("Removing trailing slash from request URI: {}", requestURI);

            request = new HttpServletRequestWrapper(httpRequest) {
                @Override
                public String getRequestURI() {
                    return requestURI.substring(0, requestURI.length() - 1);
                }
            };
        }

        chain.doFilter(request, response);
    }
}
