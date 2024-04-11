package cz.trailsthroughshadows.algorithm.session.apikey;
import cz.trailsthroughshadows.algorithm.session.Session;
import cz.trailsthroughshadows.algorithm.session.SessionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;


import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class ApiKeyAuthFilter extends AbstractAuthenticationProcessingFilter {
    private static final String HEADER_NAME = "X-API-KEY";
    private static final UUID ADMIN_API_KEY = Session.ADMINISTRATOR_SESSION.getToken();

    public ApiKeyAuthFilter(AuthenticationManager auten) {
        super("/**");
        setAuthenticationManager(auten);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        log.debug("Attempting authentication");
        String apiKey = request.getHeader(HEADER_NAME);
        log.debug("API key: {}", apiKey);
        if (apiKey.equals(ADMIN_API_KEY.toString())) {
            log.debug("Admin API key used");
            ApiKeyAuthenticationToken authRequest = new ApiKeyAuthenticationToken(ADMIN_API_KEY);
            return getAuthenticationManager().authenticate(authRequest);
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new AuthenticationServiceException("No API key in request");
        }
        // check if its uuid
        try {
            //noinspection ResultOfMethodCallIgnored
            java.util.UUID.fromString(apiKey);
        } catch (IllegalArgumentException e) {
            throw new AuthenticationServiceException("Invalid API key format");
        }

        ApiKeyAuthenticationToken authRequest = new ApiKeyAuthenticationToken(UUID.fromString(apiKey));
        return getAuthenticationManager().authenticate(authRequest);
    }
}
