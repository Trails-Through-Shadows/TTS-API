package cz.trailsthroughshadows.algorithm.session;


import cz.trailsthroughshadows.algorithm.session.credentials.AuthRequest;
import cz.trailsthroughshadows.algorithm.session.credentials.AuthResponse;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.table.playerdata.adventure.license.License;
import cz.trailsthroughshadows.api.table.playerdata.adventure.license.LicenseRepo;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@Log4j2
public class SessionHandler {

    @Autowired
    LicenseRepo licenseRepo;

    @Getter
    private HashMap<UUID, Session> sessions = new HashMap<>();

    public Session getSession(UUID uuid) {
        Session session = sessions.get(uuid);
        if (session == null) {
            throw new RestException(new RestError(HttpStatus.NOT_ACCEPTABLE, "Session not found!"));
        }
        return session;
    }

    public AuthResponse authorize(AuthRequest credentials) {
        log.debug("Authorizing session for {}", credentials.getKey());

        Optional<License> license = licenseRepo.findByKey(credentials.getKey());
        String response = "";

        if (license.isEmpty()) {
            response = "Invalid license key!";
            log.debug(response);
            throw new RestException(new RestError(HttpStatus.NOT_ACCEPTABLE, response));
        }

        if (!Objects.equals(license.get().getPassword(), credentials.getPassword())) {
            response = "Invalid password!";
            log.debug(response);
            throw new RestException(new RestError(HttpStatus.NOT_ACCEPTABLE, response));
        }

        if (license.get().getActivated() == null) {
            response = "This license isn't active!";
            log.debug(response);
            throw new RestException(new RestError(HttpStatus.NOT_ACCEPTABLE, response));
        }

        for (Session session : sessions.values()) {
            if (session.getLicenseId().equals(license.get().getId())) {
                response = "Session with this license key already exists! Returning existing session.";
                log.debug(response);
                return new AuthResponse(session.getUuid(), session.getLicenseId());
            }
        }

        UUID uuid = UUID.randomUUID();
        log.debug("Creating new session.");
        sessions.put(uuid, new Session(license.get().getId(), uuid));

        return new AuthResponse(uuid, license.get().getId());
    }
}
