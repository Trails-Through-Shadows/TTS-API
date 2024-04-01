package cz.trailsthroughshadows.algorithm.session;

import cz.trailsthroughshadows.algorithm.session.credentials.AuthRequest;
import cz.trailsthroughshadows.algorithm.session.credentials.AuthResponse;
import cz.trailsthroughshadows.algorithm.session.jwt.JwtUtil;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.rest.model.response.RestResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

   /* @PostMapping("/login")
    public ResponseEntity<AuthResponse> validatePart(@RequestBody AuthRequest creds) {
        return new ResponseEntity<>(sessionHandler.login(creds), HttpStatus.OK);
    }*/

    //TODO somewhere else
    @Getter
    public static class LoginRequest {
        private String key;
        private String password;
    }

    //https://medium.com/@minadev/authentication-and-authorization-with-spring-security-bf22e985f2cb TODO
    //TODO jebat jwt stačí api key https://stackoverflow.com/questions/54134252/how-to-config-multiple-level-authentication-for-spring-boot-restful-web-service
    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getKey(), request.getPassword()));
        String token = JwtUtil.generateToken(request.getKey());
        return ResponseEntity.ok(new AuthResponse(request.getKey(), token));
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
