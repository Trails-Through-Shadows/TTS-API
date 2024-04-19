package cz.trailsthroughshadows.algorithm.session;


import cz.trailsthroughshadows.algorithm.session.credentials.AuthRequest;
import cz.trailsthroughshadows.algorithm.session.credentials.AuthResponse;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.playerdata.adventure.AdventureRepo;
import cz.trailsthroughshadows.api.table.playerdata.license.License;
import cz.trailsthroughshadows.api.table.playerdata.license.LicenseRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Log4j2
@Component
public class SessionHandler {

    @Autowired
    LicenseRepo licenseRepo;

    @Autowired
    AdventureRepo adventureRepo;

    @Getter
    private final List<Session> sessions = new ArrayList<>();

    private void responseFail(HttpStatus status, String message, Object... args) {
        log.warn(message, args);
        throw RestException.of(HttpStatus.UNAUTHORIZED, message, args);
    }

    public boolean isSessionValid(String token) {
        return sessions.stream().anyMatch(s -> s.getToken().equals(token));
    }

    public String getTokenFromAuthHeader(String authorization) {
        if (authorization == null) {
            responseFail(HttpStatus.UNAUTHORIZED, "Authorization header is missing!");
            return null;
        }

        String[] parts = authorization.split(" ");
        if (parts.length != 2) {
            responseFail(HttpStatus.UNAUTHORIZED, "Invalid authorization header!");
            return null;
        }

        if (!parts[0].equalsIgnoreCase("Basic")) {
            responseFail(HttpStatus.UNAUTHORIZED, "Invalid authorization type!");
            return null;
        }

        return parts[1];
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return getTokenFromAuthHeader(authorization);
    }

    public Session getSession(String uuid) {
        if (Objects.equals(uuid, Session.ADMINISTRATOR_SESSION.getToken())) {
            return Session.ADMINISTRATOR_SESSION;
        }

        return sessions.stream()
                .filter(s -> s.getToken().equals(uuid))
                .findFirst()
                .orElseThrow(() -> {
                    String response = "Invalid session token!";
                    log.warn(response);
                    return RestException.of(HttpStatus.UNAUTHORIZED, response);
                });
    }

    public Session getSessionFromAuthHeader(String authorization) {
        String token = getTokenFromAuthHeader(authorization);
        return getSession(token);
    }

    public Session getSessionFromRequest(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        String token = getTokenFromAuthHeader(authorization);
        return getSession(token);
    }

    public AuthResponse login(AuthRequest credentials) {
        log.debug("Authorizing session for '{}'", credentials.getKey());

        Optional<License> license = licenseRepo.findByKey(credentials.getKey());

        if (license.isEmpty() || !Objects.equals(license.get().getPassword(), credentials.getPassword())) {
            responseFail(HttpStatus.UNAUTHORIZED, "Invalid credentials!");
            return null;
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

        String randomToken = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        Session session = new Session(randomToken, license.get().getId(), new ArrayList<>());
        log.debug("Creating new session for license #{}.", session.getLicenseId());
        sessions.add(session);

        return new AuthResponse(session);
    }

    public MessageResponse logout(String token) {
        if (sessions.stream().noneMatch(s -> s.getToken().equals(token))) {
            responseFail(HttpStatus.UNAUTHORIZED, "Invalid session token!");
        }

        Session session = getSession(token);
        sessions.remove(session);

        return new MessageResponse(HttpStatus.OK, "Logged out!");
    }
}
