package cz.trailsthroughshadows.api.table.background.race;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.action.ActionController;
import cz.trailsthroughshadows.api.table.action.model.Action;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.background.race.model.Race;
import cz.trailsthroughshadows.api.table.background.race.model.RaceDTO;
import cz.trailsthroughshadows.api.table.effect.relation.forcharacter.RaceEffect;
import cz.trailsthroughshadows.api.util.Pair;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController(value = "Race")
public class RaceController {

    @Autowired
    private ActionController actionController;

    private ValidationService validation;
    private RaceRepo clazzRepo;

    @GetMapping("/races")
    @Cacheable(value = "race")
    public ResponseEntity<RestPaginatedResult<Race>> findAllEntities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<RaceDTO> entries = clazzRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<RaceDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage.stream().map(Race::fromDTO).toList()), HttpStatus.OK);
    }

    @GetMapping("/races/{id}")
    @Cacheable(value = "race", key = "#id")
    public ResponseEntity<Race> findById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        RaceDTO entity = clazzRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Race with id '%d' not found!", id));

        if (lazy && !include.isEmpty()) {
            Initialization.hibernateInitializeAll(entity, include);
        } else if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        }

        return new ResponseEntity<>(Race.fromDTO(entity), HttpStatus.OK);
    }

    @PutMapping("races/{id}")
    @CacheEvict(value = "race", allEntries = true)
    public ResponseEntity<MessageResponse> updateRaceById(@PathVariable int id, @RequestBody RaceDTO entity) {
        validation.validate(entity);
        RaceDTO existing = clazzRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Race with id '%d' not found!", id));

        existing.setTag(entity.getTag());
        existing.setDescription(entity.getDescription());
        existing.setTitle(entity.getTitle());
        existing.setEffects(entity.getEffects());
        //TODO under constructino
        //existing.setActions(entity.getActions());
        //create or update actions
//       List<ActionDTO> actions = entity.getActions().forEach( (action) -> {
//                    ActionDTO exists = (ActionDTO) actionController.findById(action.getKey().getIdAction(), List.of(),true).getBody();
//
//                }
//             );



        clazzRepo.save(existing);

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Race updated!"), HttpStatus.OK);
    }

    @PostMapping("races/")
    @CacheEvict(value = "race", allEntries = true)
    public ResponseEntity<MessageResponse> createEntity(@RequestBody List<RaceDTO> entities) {
        entities.forEach(validation::validate);
        entities.forEach(e -> e.setId(null));

        //remove relations and save them for later
        Map<String, Pair< List<RaceAction>, List<RaceEffect>>> actions = new HashMap<>();
        entities.forEach(raceaction -> {
            actions.put(raceaction.getTag(),
                    new Pair<>(new ArrayList<>(raceaction.getActions()), new ArrayList<>(raceaction.getEffects())));
            raceaction.setActions(null);
            raceaction.setEffects(null);
        });

        entities = clazzRepo.saveAll(entities);

        entities.forEach(entity->{
            Pair<List<RaceAction>, List<RaceEffect>> pair = actions.get(entity.getTag());
            entity.setActions(new ArrayList<>(pair.first()) );
            entity.getActions().forEach(action -> action.getKey().setIdRace(action.getKey().getIdRace()));

            entity.setEffects(pair.second());
            entity.getEffects().forEach(effect -> effect.getKey().setIdRace(effect.getKey().getIdRace()));
        });

        entities = clazzRepo.saveAll(entities);

        String ids = entities.stream().map(RaceDTO::getId).map(String::valueOf).toList().toString();

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Races with ids '%s' created",ids), HttpStatus.OK);
    }


    @DeleteMapping("/races/{id}")
    @CacheEvict(value = "race", key = "#id")
    public ResponseEntity<MessageResponse> deleteEntity(@PathVariable int id){
        RaceDTO entity = clazzRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND,"Race with id '%d' not found!", id));

        clazzRepo.delete(entity);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Race with id '%d' deleted!", id),
                HttpStatus.OK);
    }



    @Autowired
    public void setRepository(RaceRepo repository) {
        this.clazzRepo = repository;
    }

    @Autowired
    public void setValidation(ValidationService validation) {
        this.validation = validation;
    }
}
