package cz.trailsthroughshadows.algorithm.session.apikey;
import cz.trailsthroughshadows.algorithm.session.SessionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.UUID;

@Slf4j
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {


    private final SessionHandler sessionHandler;

    public ApiKeyAuthenticationProvider(SessionHandler apiKeyService) {
        this.sessionHandler = apiKeyService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("Authenticating in authentification provider\n{}", authentication);
        String token = (String) authentication.getCredentials();
        log.debug("Authenticating token {}", token);
        if (sessionHandler.isSessionValid(UUID.fromString(token))) {
            Authentication auth = new ApiKeyAuthenticationToken(token, null);
            auth.setAuthenticated(true);
            return auth;
        }
        throw new AuthenticationServiceException("Invalid token");
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }
}