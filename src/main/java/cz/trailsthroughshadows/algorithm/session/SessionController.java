package cz.trailsthroughshadows.algorithm.session;

import cz.trailsthroughshadows.algorithm.session.credentials.AuthRequest;
import cz.trailsthroughshadows.algorithm.session.credentials.AuthResponse;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.model.RestResponse;
import cz.trailsthroughshadows.api.table.playerdata.adventure.AdventureRepo;
import cz.trailsthroughshadows.api.table.playerdata.adventure.model.AdventureDTO;
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

    @Autowired
    ValidationService validation;

    @Autowired
    ValidationConfig validationConfig;

    @Autowired
    AdventureRepo adventureRepo;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> validatePart(@RequestBody AuthRequest creds) {
        return new ResponseEntity<>(sessionHandler.authorize(creds), HttpStatus.OK);
    }

    @PostMapping("/hello/{token}")
    public ResponseEntity<RestResponse> hello(@RequestParam UUID token) {
        return new ResponseEntity<>(new RestResponse(HttpStatus.OK, sessionHandler.getSession(token).hello()), HttpStatus.OK);
    }

    @PutMapping("/adventure/{token}")
    public ResponseEntity<RestResponse> addAdventure(@RequestParam UUID token, @RequestBody AdventureDTO adventure) {
        Session session = sessionHandler.getSession(token);

        if (validationConfig.getLicense().getMaxAdventures() <= session.getAdventures().size()) {
            String response = "Max adventures reached!";
            log.warn(response);
            return new ResponseEntity<>(new RestResponse(HttpStatus.TOO_MANY_REQUESTS, response), HttpStatus.TOO_MANY_REQUESTS);
        }

        if (adventure.getIdLicense() != session.getLicenseId()) {
            String response = "License mismatch!";
            log.warn(response);
            return new ResponseEntity<>(new RestResponse(HttpStatus.UNAUTHORIZED, response), HttpStatus.UNAUTHORIZED);
        }

        validation.validate(adventure);

        log.debug("Saving adventure '{}' for license '{}'", adventure, session.getLicenseId());
        session.getAdventures().add(adventure);
        adventureRepo.save(adventure);

        return new ResponseEntity<>(new RestResponse(HttpStatus.OK, "Adventure added!"), HttpStatus.OK);
    }
}
