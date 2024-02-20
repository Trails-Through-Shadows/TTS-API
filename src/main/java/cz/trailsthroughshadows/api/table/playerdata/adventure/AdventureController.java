package cz.trailsthroughshadows.api.table.playerdata.adventure;

import cz.trailsthroughshadows.algorithm.session.Session;
import cz.trailsthroughshadows.algorithm.session.SessionHandler;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.MessageResponse;
import cz.trailsthroughshadows.api.rest.model.RestResponse;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.table.playerdata.adventure.model.AdventureDTO;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/adventures")
public class AdventureController {

    @Autowired
    private SessionHandler sessionHandler;

    @Autowired
    private AdventureService adventureService;

    @GetMapping("/{id}")
    public ResponseEntity<AdventureDTO> findById(
            @RequestParam UUID token,
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        Session session = sessionHandler.getSession(token);
        AdventureDTO entity = adventureService.findById(id);

        if (!Objects.equals(entity.getIdLicense(), session.getLicenseId()) && !session.isAdmin()) {
            throw new RestException(RestError.of(HttpStatus.FORBIDDEN, "You are not authorized to access this resource!"));
        }

        if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        } else {
            Initialization.hibernateInitializeAll(entity, include);
        }

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<RestPaginatedResult<AdventureDTO>> findAllEntities(
            @RequestParam UUID token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        Session session = sessionHandler.getSession(token);

        List<AdventureDTO> entries = adventureService.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))) &&
                        Objects.equals(entry.getIdLicense(), session.getLicenseId()) || session.isAdmin())
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<AdventureDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        if (!lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entriesPage.size(), false, entriesPage.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<RestResponse> addAdventure(@RequestParam UUID token, @RequestBody AdventureDTO adventure) {
        return new ResponseEntity<>(adventureService.addAdventure(adventure, sessionHandler.getSession(token)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteAdventure(@RequestParam UUID token, @PathVariable int id) {
        return new ResponseEntity<>(adventureService.deleteAdventure(id, sessionHandler.getSession(token)), HttpStatus.OK);
    }
}
