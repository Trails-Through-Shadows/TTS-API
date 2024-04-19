package cz.trailsthroughshadows.api.configuration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cz.trailsthroughshadows.algorithm.session.Session;
import cz.trailsthroughshadows.algorithm.session.SessionHandler;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class AuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private SessionHandler sessionHandler;

    private final List<String> ignoredPaths = List.of("/session/login", "/swagger-ui", "/api-docs");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // Ignore SessionController
        if (ignoredPaths.stream().anyMatch(path -> request.getRequestURI().contains(path))) {
            log.info("Ignoring session controller.");
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization header is missing
        if (authHeader == null) {
            log.warn("Authorization header is missing.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            try {
                JsonElement jsonResponse = new JsonObject();
                jsonResponse.getAsJsonObject().addProperty("message", "Authorization header is missing.");
                response.getWriter().write(jsonResponse.toString());
            } catch (Exception e) {
                log.error("Failed to write JSON response.");
            }

            return;
        }

        String token = null;
        try {
            token = sessionHandler.getTokenFromAuthHeader(authHeader);
        } catch (RestException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
            return;
        }

        // Bypass authorization for administration session
        if (token.equals(Session.ADMINISTRATOR_SESSION.getToken())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Token is valid
        if (sessionHandler.isSessionValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        try {
            JsonElement jsonResponse = new JsonObject();
            jsonResponse.getAsJsonObject().addProperty("message", "Invalid session token.");
            response.getWriter().write(jsonResponse.toString());
        } catch (Exception e) {
            log.error("Failed to write JSON response.");
        }
    }
}
