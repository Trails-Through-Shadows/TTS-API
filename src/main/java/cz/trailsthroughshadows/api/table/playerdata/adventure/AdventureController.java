package cz.trailsthroughshadows.api.table.playerdata.adventure;

import cz.trailsthroughshadows.algorithm.session.Session;
import cz.trailsthroughshadows.algorithm.session.SessionHandler;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.RestResponse;
import cz.trailsthroughshadows.api.table.playerdata.adventure.model.Adventure;
import cz.trailsthroughshadows.api.table.playerdata.adventure.model.AdventureDTO;
import cz.trailsthroughshadows.api.table.playerdata.character.CharacterService;
import cz.trailsthroughshadows.api.table.playerdata.character.model.Character;
import cz.trailsthroughshadows.api.table.playerdata.character.model.CharacterDTO;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adventures")
public class AdventureController {

    @Autowired
    private SessionHandler sessionHandler;

    @Autowired
    private AdventureService adventureService;

    @Autowired
    private CharacterService characterService;

    @GetMapping("/{id}")
    public ResponseEntity<Adventure> findById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy,
            @RequestHeader(name = "Authorization") String authorization
    ) {
        Session session = sessionHandler.getSessionFromAuthHeader(authorization);
        AdventureDTO entity = adventureService.findById(id);

        if (!session.hasAccess(entity.getIdLicense())) {
            throw new RestException(RestError.of(HttpStatus.FORBIDDEN, "You are not authorized to access this resource!"));
        }

        if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        } else {
            Initialization.hibernateInitializeAll(entity, include);
        }
        Adventure res = Adventure.fromDTO(entity);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<RestPaginatedResult<AdventureDTO>> findAllEntities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "true") boolean lazy,
            @RequestHeader(name = "Authorization") String authorization
    ) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        Session session = sessionHandler.getSessionFromAuthHeader(authorization);

        List<AdventureDTO> entries = adventureService.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))) &&
                        session.hasAccess(entry.getIdLicense()))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<AdventureDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entriesPage.size(), false, entriesPage.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage), HttpStatus.OK);
    }

    @PostMapping("/{idLicense}")
    public ResponseEntity<RestResponse> addAdventure(
            @PathVariable int idLicense,
            @RequestBody AdventureDTO adventure,
            @RequestHeader(name = "Authorization") String authorization
    ) {
        return new ResponseEntity<>(adventureService.add(adventure, idLicense, sessionHandler.getSessionFromAuthHeader(authorization)), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestResponse> updateAdventure(
            @PathVariable int id,
            @RequestBody AdventureDTO adventure,
            @RequestHeader(name = "Authorization") String authorization
    ) {
        return new ResponseEntity<>(adventureService.update(id, adventure, sessionHandler.getSessionFromAuthHeader(authorization)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponse> deleteAdventure(
            @PathVariable int id,
            @RequestHeader(name = "Authorization") String authorization
    ) {
        return new ResponseEntity<>(adventureService.delete(id, sessionHandler.getSessionFromAuthHeader(authorization)), HttpStatus.OK);
    }

    @GetMapping("/{id}/characters")
    public ResponseEntity<RestPaginatedResult<Character>> findAllEntities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "true") boolean lazy,
            @PathVariable int id,
            @RequestHeader(name = "Authorization") String authorization
    ) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        Session session = sessionHandler.getSessionFromAuthHeader(authorization);

        List<CharacterDTO> entries = characterService.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))) &&
                        session.hasAccess(adventureService.findById(entry.getIdAdventure()).getIdLicense())
                        && entry.getIdAdventure() == id)
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<CharacterDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }
        List<Character> characters = entriesPage.stream().map(Character::fromDTO).toList();

        Pagination pagination = new Pagination(entriesPage.size(), false, entriesPage.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, characters), HttpStatus.OK);
    }

    @PostMapping("/{id}/characters")
    public ResponseEntity<RestResponse> addCharacter(
            @PathVariable int id,
            @RequestBody CharacterDTO character,
            @RequestHeader(name = "Authorization") String authorization
    ) {
        return new ResponseEntity<>(characterService.add(character, id, sessionHandler.getSessionFromAuthHeader(authorization)), HttpStatus.OK);
    }
}
