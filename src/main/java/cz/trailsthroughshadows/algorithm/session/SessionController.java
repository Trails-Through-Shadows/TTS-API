package cz.trailsthroughshadows.algorithm.session;

import cz.trailsthroughshadows.algorithm.session.credentials.AuthRequest;
import cz.trailsthroughshadows.algorithm.session.credentials.AuthResponse;
import cz.trailsthroughshadows.api.rest.model.MessageResponse;
import cz.trailsthroughshadows.api.rest.model.RestResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    public ResponseEntity<RestResponse> logout(@RequestParam UUID token) {
        return new ResponseEntity<>(sessionHandler.logout(token), HttpStatus.OK);
    }

    @GetMapping("/hello")
    public ResponseEntity<MessageResponse> hello(@RequestParam UUID token) {
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, sessionHandler.getSession(token).hello()), HttpStatus.OK);
    }
}
