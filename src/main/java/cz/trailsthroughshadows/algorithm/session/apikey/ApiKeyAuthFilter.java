package cz.trailsthroughshadows.algorithm.session.apikey;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.filter.GenericFilterBean;


import java.io.IOException;

@Slf4j
public class ApiKeyAuthFilter extends AbstractAuthenticationProcessingFilter {
    private static final String HEADER_NAME = "X-API-KEY";

    public ApiKeyAuthFilter(AuthenticationManager auten) {
        super("/**");
        setAuthenticationManager(auten);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        log.debug("Attempting authentication");
        String apiKey = request.getHeader(HEADER_NAME);
        log.debug("API key: {}", apiKey);
        if (apiKey == null) {
            throw new AuthenticationServiceException("No API key in request");
        }
        ApiKeyAuthenticationToken authRequest = new ApiKeyAuthenticationToken(apiKey);
        return getAuthenticationManager().authenticate(authRequest);
    }
}
