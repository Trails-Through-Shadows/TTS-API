package cz.trailsthroughshadows.algorithm.session.apikey;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.filter.GenericFilterBean;


import java.io.IOException;

public class ApiKeyAuthFilter extends AbstractAuthenticationProcessingFilter {
    private static final String HEADER_NAME = "X-API-KEY";

    public ApiKeyAuthFilter() {
        super("/api/**");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String apiKey = request.getHeader(HEADER_NAME);
        if (apiKey == null) {
            throw new AuthenticationServiceException("No API key in request");
        }
        ApiKeyAuthenticationToken authRequest = new ApiKeyAuthenticationToken(apiKey, null);
        return getAuthenticationManager().authenticate(authRequest);
    }
}
