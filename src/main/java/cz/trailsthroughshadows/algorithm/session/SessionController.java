package cz.trailsthroughshadows.algorithm.session;

import cz.trailsthroughshadows.algorithm.session.credentials.AuthRequest;
import cz.trailsthroughshadows.api.rest.model.RestResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Log4j2
@Component
@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    private SessionHandler sessionHandler;

    //region Auth

    @PostMapping("/login")
    public ResponseEntity<RestResponse> validatePart(@RequestBody AuthRequest creds) {
        return new ResponseEntity<>(new RestResponse(HttpStatus.OK, sessionHandler.authorize(creds).toString()), HttpStatus.OK);
    }

    @PostMapping("/hello")
    public ResponseEntity<RestResponse> hello(@RequestBody UUID uuid) {
        return new ResponseEntity<>(new RestResponse(HttpStatus.OK, sessionHandler.getSession(uuid).hello()), HttpStatus.OK);
    }

    //endregion
}
