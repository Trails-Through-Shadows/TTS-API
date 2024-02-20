package cz.trailsthroughshadows.api.table.background;

import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.table.background.clazz.ClazzRepo;
import cz.trailsthroughshadows.api.table.background.clazz.model.ClazzDTO;
import cz.trailsthroughshadows.api.table.background.race.RaceRepo;
import cz.trailsthroughshadows.api.table.background.race.model.RaceDTO;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/background/")
public class BackgroundController {

    @Autowired
    private ClazzRepo clazzRepo;

    @Autowired
    private RaceRepo raceRepo;

    @GetMapping("classes/{id}")
    public ClazzDTO findClazzById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        ClazzDTO entity = clazzRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '%d' not found! " + id));

        if (!lazy)
            Initialization.hibernateInitializeAll(entity);
        else
            Initialization.hibernateInitializeAll(entity, include);

        return entity;

    }

    @GetMapping("classes")
    public ResponseEntity<RestPaginatedResult<ClazzDTO>> findAllClasses(

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        Collection<ClazzDTO> entities = clazzRepo.findAll();

        if (!lazy && !include.isEmpty()) {
            entities.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entities.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entities.size(), false, entities.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entities), HttpStatus.OK);
    }

    // RACE SECTION

    @GetMapping("races/{id}")
    public RaceDTO findRaceById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        RaceDTO entity = raceRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '%d' not found! " + id));

        if (!lazy)
            Initialization.hibernateInitializeAll(entity);
        else
            Initialization.hibernateInitializeAll(entity, include);

        return entity;
    }

    @GetMapping("races")
    public ResponseEntity<RestPaginatedResult<RaceDTO>> findAllRaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        Collection<RaceDTO> entities = raceRepo.findAll();

        if (!lazy && !include.isEmpty()) {
            entities.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entities.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entities.size(), false, entities.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entities), HttpStatus.OK);
    }
}
