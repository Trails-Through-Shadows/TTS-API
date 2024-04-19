package cz.trailsthroughshadows.algorithm.session;

import cz.trailsthroughshadows.algorithm.session.credentials.AuthRequest;
import cz.trailsthroughshadows.algorithm.session.credentials.AuthResponse;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.rest.model.response.RestResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Log4j2
@Component
@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    private SessionHandler sessionHandler;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> validatePart(@RequestBody AuthRequest creds) {
        return new ResponseEntity<>(sessionHandler.login(creds), HttpStatus.OK);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<RestResponse> logout(
            @RequestHeader(name = "Authorization") String authorization
    ) {
        String token = sessionHandler.getTokenFromAuthHeader(authorization);
        return new ResponseEntity<>(sessionHandler.logout(token), HttpStatus.OK);
    }

    @GetMapping("/hello")
    public ResponseEntity<MessageResponse> hello(
            @RequestHeader(name = "Authorization") String authorization
    ) {
        String token = sessionHandler.getTokenFromAuthHeader(authorization);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, sessionHandler.getSession(token).hello()), HttpStatus.OK);
    }
}
