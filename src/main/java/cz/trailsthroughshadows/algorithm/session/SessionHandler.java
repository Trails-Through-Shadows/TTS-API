package cz.trailsthroughshadows.algorithm.session;


import cz.trailsthroughshadows.algorithm.session.credentials.AuthRequest;
import cz.trailsthroughshadows.algorithm.session.credentials.AuthResponse;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.playerdata.adventure.AdventureRepo;
import cz.trailsthroughshadows.api.table.playerdata.license.License;
import cz.trailsthroughshadows.api.table.playerdata.license.LicenseRepo;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Log4j2
public class SessionHandler {

    @Autowired
    LicenseRepo licenseRepo;

    @Autowired
    AdventureRepo adventureRepo;

    @Getter
    private List<Session> sessions = new ArrayList<>();

    private void responseFail(HttpStatus status, String message, Object... args) {
        log.warn(message, args);
        throw RestException.of(HttpStatus.UNAUTHORIZED, message, args);
    }

    public boolean isSessionValid(UUID token) {
        return sessions.stream().anyMatch(s -> s.getToken().equals(token));
    }

    public Session getSession(UUID uuid) {

        return sessions.stream()
                .filter(s -> s.getToken().equals(uuid))
                .findFirst()
                .orElseThrow(() -> {
                    String response = "Invalid session token!";
                    log.warn(response);
                    return RestException.of(HttpStatus.UNAUTHORIZED, response);
                });
    }

    public AuthResponse login(AuthRequest credentials) {
        log.debug("Authorizing session for '{}'", credentials.getKey());

        Optional<License> license = licenseRepo.findByKey(credentials.getKey());

        if (license.isEmpty()) {
            responseFail(HttpStatus.UNAUTHORIZED, "License '{}' not found!", credentials.getKey());
        }

        if (!Objects.equals(license.get().getPassword(), credentials.getPassword())) {
            responseFail(HttpStatus.UNAUTHORIZED, "Invalid password!");
        }

        if (license.get().getActivated() == null) {
            log.debug("Activating a new license '{}'!", license.get().getKey());
            licenseRepo.activate(license.get().getKey());
        }

        Optional<Session> optionalSession = sessions.stream()
                .filter(s -> s.getLicenseId().equals(license.get().getId()))
                .findFirst();

        if (optionalSession.isPresent()) {
            log.debug("Session with this license key already exists! Returning existing session #{}.", optionalSession.get().getLicenseId());
            return new AuthResponse(optionalSession.get());
        }

        Session session = new Session(UUID.randomUUID(), license.get().getId(), new ArrayList<>());
        log.debug("Creating new session for license #{}.", session.getLicenseId());
        sessions.add(session);
        return new AuthResponse(session);
    }

    public MessageResponse logout(UUID token) {
        if (sessions.stream().noneMatch(s -> s.getToken().equals(token))) {
            responseFail(HttpStatus.UNAUTHORIZED, "Invalid session token!");
        }
        sessions.removeIf(s -> s.getToken().equals(token));
        return new MessageResponse(HttpStatus.OK, "Logged out!");
    }


}
