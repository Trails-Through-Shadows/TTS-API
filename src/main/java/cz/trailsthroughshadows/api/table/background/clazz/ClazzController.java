package cz.trailsthroughshadows.api.table.background.clazz;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.action.ActionRepo;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.background.clazz.model.Clazz;
import cz.trailsthroughshadows.api.table.background.clazz.model.ClazzDTO;
import cz.trailsthroughshadows.api.table.effect.EffectRepo;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.forcharacter.ClazzEffect;
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
@RestController(value = "Class")
public class ClazzController {

    private ValidationService validation;
    private ClazzRepo clazzRepo;
    private EffectRepo effectRepo;
    private ActionRepo actionRepo;

    @GetMapping("/classes")
    @Cacheable(value = "class")
    public ResponseEntity<RestPaginatedResult<Clazz>> getEnemies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<ClazzDTO> entries = clazzRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<ClazzDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage.stream().map(Clazz::fromDTO).toList()), HttpStatus.OK);
    }

    @GetMapping("/classes/{id}")
    @Cacheable(value = "class", key = "#id")
    public ResponseEntity<Clazz> findById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        ClazzDTO entity = clazzRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Class with id '%d' not found! ", id));

        if (lazy && !include.isEmpty()) {
            Initialization.hibernateInitializeAll(entity, include);
        } else if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        }

        return new ResponseEntity<>(Clazz.fromDTO(entity), HttpStatus.OK);
    }

    @PutMapping("classes/{id}")
    @CacheEvict(value = "class", key = "#id")
    public ResponseEntity<MessageResponse> updateEntity(
            @PathVariable int id,
            @RequestBody ClazzDTO entity
    ) {
        log.debug("Updating class with id: " + id);

        // Validate class
        validation.validate(entity);

        ClazzDTO clazzToUpdate = clazzRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Class with id '%d' not found! ", id));

        // Remove relations and save them for later
        List<ClazzEffect> entityEffects = new ArrayList<>();
        if (entity.getEffects() != null) {
            entityEffects.addAll(entity.getEffects());
            entity.getEffects().clear();
        }

        List<ClazzAction> entityActions = new ArrayList<>();
        if (entity.getActions() != null) {
            entityActions.addAll(entity.getActions());
            entity.getActions().clear();
        }

        // Update enemy
        clazzToUpdate.setTitle(entity.getTitle());
        clazzToUpdate.setTag(entity.getTag());
        clazzToUpdate.setDescription(entity.getDescription());
        clazzToUpdate.setBaseHealth(entity.getBaseHealth());
        clazzToUpdate.setBaseDefence(entity.getBaseDefence());
        clazzToUpdate.setBaseInitiative(entity.getBaseInitiative());
        clazzToUpdate = clazzRepo.save(clazzToUpdate);

        // Post load relations
        if (clazzToUpdate.getEffects() != null) {
            for (ClazzEffect effect : entityEffects) {
                EffectDTO effectDTO = processEffects(effect.getEffect());

                effect.setKey(new ClazzEffect.ClazzEffectId(clazzToUpdate.getId(), effectDTO.getId()));
                effect.setEffect(effectDTO);
            }

            clazzToUpdate.getEffects().clear();
            clazzToUpdate.getEffects().addAll(entityEffects);
        }

        if (clazzToUpdate.getActions() != null) {
            for (ClazzAction action : entityActions) {
                ActionDTO actionDTO = actionRepo.findById(action.getKey().getIdAction())
                        .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '%d' not found! ", action.getKey().getIdAction()));

                action.setKey(new ClazzAction.ClazzActionId(clazzToUpdate.getId(), actionDTO.getId()));
                action.setAction(actionDTO);
            }

            clazzToUpdate.getActions().clear();
            clazzToUpdate.getActions().addAll(entityActions);
        }

        if (!entityEffects.isEmpty() || !entityActions.isEmpty()) {
            clazzRepo.save(clazzToUpdate);
        }

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Class with id '%d' updated!", id), HttpStatus.OK);
    }

    @PostMapping("/classes")
    @CacheEvict(value = "class", allEntries = true)
    public ResponseEntity<MessageResponse> createEntity(
            @RequestBody List<ClazzDTO> clazzes
    ) {
        // Validate enemies
        clazzes.forEach(validation::validate);

        // Remove ids to prevent conflicts
        clazzes.forEach((e) -> e.setId(null));

        // Remove relations and save them for later
        Map<String, Pair<List<ClazzAction>, List<ClazzEffect>>> actionsAndEffects = new HashMap<>();
        clazzes.forEach((e) -> {
            actionsAndEffects.put(e.getTag(), new Pair<>(new ArrayList<>(e.getActions()), new ArrayList<>(e.getEffects())));
            e.setActions(null);
            e.setEffects(null);
        });

        // Save classes
        clazzes = clazzRepo.saveAll(clazzes);

        // Load relations
        clazzes.forEach((e) -> {
            Pair<List<ClazzAction>, List<ClazzEffect>> pair = actionsAndEffects.get(e.getTag());

            e.setActions(new ArrayList<>(pair.first()));
            e.getActions().forEach(action -> action.getKey().setIdClass(e.getId()));

            e.setEffects(new ArrayList<>(pair.second()));
            e.getEffects().forEach(effect -> effect.getKey().setIdClass(e.getId()));
        });

        // Save clazz relations
        clazzes = clazzRepo.saveAll(clazzes);

        String ids = clazzes.stream().map((entry) -> String.valueOf(entry.getId())).toList().toString();
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.CREATED, "Classes with ids '%d' created!", ids), HttpStatus.CREATED);
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

    @DeleteMapping("/classes/{id}")
    @CacheEvict(value = "class", key = "#id")
    public ResponseEntity<MessageResponse> deleteEntity(
            @PathVariable int id
    ) {
        ClazzDTO entity = clazzRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Class with id '%d' not found! ", id));

        clazzRepo.delete(entity);

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Class with id '%d' deleted!", id), HttpStatus.OK);
    }

    @Autowired
    public void setRepository(ClazzRepo repository) {
        this.clazzRepo = repository;
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
