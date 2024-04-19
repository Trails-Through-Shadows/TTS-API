package cz.trailsthroughshadows.api.table.background.race;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.action.ActionRepo;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.background.race.model.Race;
import cz.trailsthroughshadows.api.table.background.race.model.RaceDTO;
import cz.trailsthroughshadows.api.table.effect.EffectRepo;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
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

    private ValidationService validation;
    private RaceRepo raceRepo;
    private EffectRepo effectRepo;
    private ActionRepo actionRepo;

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

        List<RaceDTO> entries = raceRepo.findAll().stream()
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
        RaceDTO entity = raceRepo
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
    @CacheEvict(value = "race", key = "#id")
    public ResponseEntity<MessageResponse> updateRaceById(
            @PathVariable int id,
            @RequestBody RaceDTO entity
    ) {
        log.debug("Updating race with id: " + id);

        // Validate race
        validation.validate(entity);

        RaceDTO raceToUpdate = raceRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Race with id '%d' not found! ", id));

        // Remove relations and save them for later
        List<RaceEffect> entityEffects = new ArrayList<>();
        if (entity.getEffects() != null) {
            entityEffects.addAll(entity.getEffects());
            entity.getEffects().clear();
        }

        List<RaceAction> entityActions = new ArrayList<>();
        if (entity.getActions() != null) {
            entityActions.addAll(entity.getActions());
            entity.getActions().clear();
        }

        // Updated race
        raceToUpdate.setTag(entity.getTag());
        raceToUpdate.setDescription(entity.getDescription());
        raceToUpdate.setTitle(entity.getTitle());
        raceToUpdate = raceRepo.save(raceToUpdate);

        // Post load relations
        if (raceToUpdate.getEffects() != null) {
            for (RaceEffect effect : entityEffects) {
                EffectDTO effectDTO = processEffects(effect.getEffect());

                effect.setKey(new RaceEffect.RaceEffectId(raceToUpdate.getId(), effectDTO.getId()));
                effect.setEffect(effectDTO);
            }

            raceToUpdate.getEffects().clear();
            raceToUpdate.getEffects().addAll(entityEffects);
        }

        if (raceToUpdate.getActions() != null) {
            for (RaceAction action : entityActions) {
                ActionDTO actionDTO = actionRepo.findById(action.getKey().getIdAction())
                        .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '%d' not found! ", action.getKey().getIdAction()));

                action.setKey(new RaceAction.RaceActionId(raceToUpdate.getId(), actionDTO.getId()));
                action.setAction(actionDTO);
            }

            raceToUpdate.getActions().clear();
            raceToUpdate.getActions().addAll(entityActions);
        }

        if (!entityEffects.isEmpty() || !entityActions.isEmpty()) {
            raceRepo.save(raceToUpdate);
        }

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Race with id '%d' updated!", id), HttpStatus.OK);
    }

    @PostMapping("races/")
    @CacheEvict(value = "race", allEntries = true)
    public ResponseEntity<MessageResponse> createEntity(
            @RequestBody List<RaceDTO> races
    ) {
        // Validate enemies
        races.forEach(validation::validate);

        // Remove ids to prevent conflicts
        races.forEach(e -> e.setId(null));

        //remove relations and save them for later
        Map<String, Pair<List<RaceAction>, List<RaceEffect>>> actionsAndEffects = new HashMap<>();
        races.forEach(raceaction -> {
            actionsAndEffects.put(raceaction.getTag(), new Pair<>(new ArrayList<>(raceaction.getActions()), new ArrayList<>(raceaction.getEffects())));
            raceaction.setActions(null);
            raceaction.setEffects(null);
        });

        // Save classes
        races = raceRepo.saveAll(races);

        // Load relations
        races.forEach(entity -> {
            Pair<List<RaceAction>, List<RaceEffect>> pair = actionsAndEffects.get(entity.getTag());

            entity.setActions(new ArrayList<>(pair.first()));
            entity.getActions().forEach(action -> action.getKey().setIdRace(action.getKey().getIdRace()));

            entity.setEffects(new ArrayList<>(pair.second()));
            entity.getEffects().forEach(effect -> effect.getKey().setIdRace(effect.getKey().getIdRace()));
        });

        // Save race relations
        races = raceRepo.saveAll(races);

        String ids = races.stream().map(RaceDTO::getId).map(String::valueOf).toList().toString();
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Races with ids '%s' created", ids), HttpStatus.OK);
    }

    private EffectDTO processEffects(EffectDTO inputEffect) {
        List<EffectDTO> effects = effectRepo.findUnique(
                inputEffect.getTarget(),
                inputEffect.getType(),
                inputEffect.getDuration(),
                inputEffect.getStrength()
        );

        EffectDTO effect = null;
        if (effects.isEmpty()) {
            log.info("Effect {} not found, creating new", inputEffect);
            effect = effectRepo.saveAndFlush(inputEffect);
        } else {
            log.info("Effect {} found", inputEffect);
            effect = effects.getFirst();
        }

        return effect;
    }

    @DeleteMapping("/races/{id}")
    @CacheEvict(value = "race", key = "#id")
    public ResponseEntity<MessageResponse> deleteEntity(@PathVariable int id) {
        RaceDTO entity = raceRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Race with id '%d' not found!", id));

        raceRepo.delete(entity);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Race with id '%d' deleted!", id), HttpStatus.OK);
    }

    @Autowired
    public void setRepository(RaceRepo repository) {
        this.raceRepo = repository;
    }

    @Autowired
    public void setEffectRepo(EffectRepo effectRepo) {
        this.effectRepo = effectRepo;
    }

    @Autowired
    public void setActionRepo(ActionRepo actionRepo) {
        this.actionRepo = actionRepo;
    }

    @Autowired
    public void setValidation(ValidationService validation) {
        this.validation = validation;
    }
}
