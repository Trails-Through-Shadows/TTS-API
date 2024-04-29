package cz.trailsthroughshadows.algorithm.encounter;

import cz.trailsthroughshadows.algorithm.session.Session;
import cz.trailsthroughshadows.algorithm.session.SessionHandler;
import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.table.playerdata.adventure.AdventureRepo;
import cz.trailsthroughshadows.api.table.playerdata.adventure.model.Adventure;
import cz.trailsthroughshadows.api.table.playerdata.adventure.model.AdventureDTO;
import cz.trailsthroughshadows.api.table.schematic.location.LocationRepo;
import cz.trailsthroughshadows.api.table.schematic.location.model.Location;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class EncounterHandler {

    @Autowired
    private LocationRepo locationRepo;

    @Autowired
    private SessionHandler sessionHandler;

    @Autowired
    private AdventureRepo adventureRepo;

    @Autowired
    ValidationService validation;

    @Value("${encounterRate.in.seconds}")
    private long fixedRate;

    @Getter
    private final List<Encounter> encounters = new ArrayList<>();

    private Integer getNextId() {
        for (int i = 1; i < encounters.size() + 1; i++) {
            int finalI = i;
            if (encounters.stream().noneMatch(e -> e.getId().equals(finalI))) {
                return i;
            }
        }
        return encounters.size() + 1;
    }

    public List<Encounter> getAllEncounters(String token, Integer idAdventure) {
        return encounters.stream()
                .filter(e -> sessionHandler.getSession(token).hasAccess(e.getIdLicense()))
                .filter(e -> idAdventure == null || idAdventure == 0 || e.getAdventure().getId().equals(idAdventure))
                .toList();
    }

    public Encounter getEncounter(String token, Integer id) {
        Encounter[] enc = new Encounter[1];

        return encounters.stream()
                .filter(e -> e.getId().equals(id) && sessionHandler.getSession(token).hasAccess(e.getIdLicense()))
                .peek(e -> enc[0] = e)
                .findFirst()
                .orElseThrow(() -> {
                    if (enc[0] != null && sessionHandler.getSession(token).hasAccess(enc[0].getIdLicense())) {
                        String response = "Unauthorized access to encounter!";
                        log.warn(response);
                        return RestException.of(HttpStatus.UNAUTHORIZED, response);
                    } else {
                        String response = "Encounter not found!";
                        log.warn(response);
                        return RestException.of(HttpStatus.NOT_FOUND, response);
                    }
                });
    }

    public Integer addEncounter(String token, Integer idAdventure, Integer idLocation) {

        AdventureDTO adventure = adventureRepo.findById(idAdventure)
                .orElseThrow(() -> {
                    String response = "Adventure not found!";
                    log.warn(response);
                    return RestException.of(HttpStatus.NOT_FOUND, response);
                });


        if (!sessionHandler.getSession(token).hasAccess(adventure.getIdLicense())) {
            String response = "Unauthorized access to adventure!";
            log.warn(response);
            throw RestException.of(HttpStatus.UNAUTHORIZED, response);
        }

        LocationDTO location = locationRepo.findById(idLocation)
                .orElseThrow(() -> {
                    String response = "Location not found!";
                    log.warn(response);
                    return RestException.of(HttpStatus.NOT_FOUND, response);
                });

        Encounter encounter = new Encounter(getNextId(), adventure.getIdLicense(), Adventure.fromDTO(adventure), Location.fromDTO(location));
        encounter.setValidation(validation);
        encounters.add(encounter);
        log.info("Encounter added: {}", encounter.getId());

        return encounter.getId();
    }

    public void removeEncounter(String  token, Integer id) {
        Encounter encounter = getEncounter(token, id);
        encounters.remove(encounter);
        log.info("Encounter removed: {}", encounter.getId());
    }

    @Scheduled(fixedRateString = "${encounterRate.in.seconds}", timeUnit = TimeUnit.SECONDS)
    public void cleanEncounters() {
        Date now = new Date();
        log.debug("Current encounter count: {}", encounters.size());
        for (Encounter encounter : new ArrayList<>(encounters)) {
            if (now.getTime() - encounter.getLastAccess().getTime() > fixedRate/1000) {
                log.debug("  Removing expired encounter #{}.", encounter.getId());
                encounters.remove(encounter);
            }
            if (encounter.getState() == Encounter.EncounterState.FAILED || encounter.getState() == Encounter.EncounterState.COMPLETED) {
                log.debug("  Removing encounter #{} with state {}.", encounter.getId(), encounter.getState());
                encounters.remove(encounter);
            }
        }
    }

}
