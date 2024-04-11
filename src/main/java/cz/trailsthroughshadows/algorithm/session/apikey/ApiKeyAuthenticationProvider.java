package cz.trailsthroughshadows.algorithm.session.apikey;
import cz.trailsthroughshadows.algorithm.session.SessionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
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
        UUID token = (UUID) authentication.getCredentials();
        log.debug("Authenticating token {}", token);
        if (sessionHandler.isSessionValid(token)) {
            log.debug("Token is valid");

            // Create a list of GrantedAuthority objects
            List<GrantedAuthority> authorities = new ArrayList<>();

            // Add a new SimpleGrantedAuthority to the list
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            // If the token belongs to an admin, add the ROLE_ADMIN authority
            if (sessionHandler.getSession(token).isAdmin()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }

            // Pass the list of authorities to the ApiKeyAuthenticationToken constructor
            Authentication auth = new ApiKeyAuthenticationToken(token, authorities);
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
