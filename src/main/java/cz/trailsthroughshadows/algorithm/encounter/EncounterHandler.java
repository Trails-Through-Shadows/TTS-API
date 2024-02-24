package cz.trailsthroughshadows.algorithm.encounter;

import cz.trailsthroughshadows.algorithm.session.SessionHandler;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.table.playerdata.adventure.AdventureRepo;
import cz.trailsthroughshadows.api.table.playerdata.adventure.model.AdventureDTO;
import cz.trailsthroughshadows.api.table.schematic.location.LocationRepo;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class EncounterHandler {

    @Autowired
    private LocationRepo locationRepo;

    @Autowired
    private SessionHandler sessionHandler;

    @Autowired
    private AdventureRepo adventureRepo;

    @Getter
    private List<Encounter> encounters = new ArrayList<>();

    private Integer getNextId() {
        for (int i = 1; i < encounters.size() + 1; i++) {
            if (encounters.get(i).getId() != i) {
                return i;
            }
        }
        return encounters.size() + 2;
    }

    public Encounter getEncounter(UUID token, Integer id) {
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

    public Integer addEncounter(UUID token, Integer idAdventure, Integer idLocation) {

        AdventureDTO adventure = adventureRepo.findById(idAdventure)
                .orElseThrow(() -> {
                    String response = "Adventure not found!";
                    log.warn(response);
                    return RestException.of(HttpStatus.NOT_FOUND, response);
                });
        Initialization.hibernateInitializeAll(adventure, List.of("characters", "clazz", "effects"));

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

        // todo add entities to constructor

        Encounter encounter = new Encounter(getNextId(), adventure.getIdLicense(), adventure, location, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        encounters.add(encounter);
        log.info("Encounter added: {}", encounter.getId());

        return encounter.getId();
    }

    public void removeEncounter(UUID token, Integer id) {
        Encounter encounter = getEncounter(token, id);
        encounters.remove(encounter);
        log.info("Encounter removed: {}", encounter.getId());
    }


}
