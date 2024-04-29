package cz.trailsthroughshadows.api.table.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.action.model.Action;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.background.clazz.ClazzRepo;
import cz.trailsthroughshadows.api.table.background.clazz.model.Clazz;
import cz.trailsthroughshadows.api.table.background.race.RaceRepo;
import cz.trailsthroughshadows.api.table.background.race.model.Race;
import cz.trailsthroughshadows.api.table.effect.EffectRepo;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.foraction.AttackEffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.foraction.MovementEffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.foraction.SkillEffectDTO;
import cz.trailsthroughshadows.api.table.enemy.EnemyRepo;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@RestController(value = "Action")
@RestControllerAdvice
public class ActionController {

    private ValidationService validation;
    private ActionRepo actionRepo;
    private EffectRepo effectRepo;

    @Autowired
    private EnemyRepo enemyRepo;

    @Autowired
    private ClazzRepo clazzRepo;

    @Autowired
    private RaceRepo raceRepo;

    @GetMapping("/actions")
    @Cacheable(value = "action", key="T(java.util.Objects).hash(#page, #limit, #filter, #sort, #include, #lazy)")
    public ResponseEntity<RestPaginatedResult<Action>> findAllEntities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<ActionDTO> entries = actionRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<ActionDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage.stream().map(Action::fromDTO).toList()), HttpStatus.OK);
    }

    @Operation(
            summary = "Get a action by its id",
            description = """
                    # Get a action by its id
                    By default it loads all fields and returns them

                    Action contains attack, movement, skill, restoreCards, summonActions and some of them contains effects

                    - **attack** - contains effects
                    - **movement** - contains effects
                    - **skill** - contains effects
                    - **restoreCards** - doesnt contains effects
                    - **summonActions** - contains effects
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the action",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Action.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Action not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/actions/{id}")
    @Cacheable(value = "action", key="T(java.util.Objects).hash(#id, #include, #lazy)")
    public ResponseEntity<Action> findById(
            @Parameter(description = "Id of the action to be obtained.\n\n Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "Case sensitive fields which you want to be loaded", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "- **false** - all fields are loaded \n - **true** - loaded only things that are in include", required = false)
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        ActionDTO entity = actionRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '{}' not found!", id));

        if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        } else {
            Initialization.hibernateInitializeAll(entity, include);
        }

        return new ResponseEntity<>(Action.fromDTO(entity), HttpStatus.OK);
    }

    /**
     * helper thing for effects
     *
     * @param inputEffect
     * @return
     */
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
            effect = effectRepo.save(inputEffect);
        } else {
            log.info("Effect {} found", inputEffect);
            effect = effects.getFirst();
        }

        return effect;
    }

    /**
     * should update or create everything in action
     *
     * @param id
     * @param action
     * @return updated action
     */
    public ActionDTO updateInner(int id, ActionDTO action) {

        ActionDTO entityToUpdate = actionRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '{}' not found!", id));

        entityToUpdate.setTitle(action.getTitle());
        entityToUpdate.setDescription(action.getDescription());
        entityToUpdate.setDiscard(action.getDiscard());
        entityToUpdate.setLevelReq(action.getLevelReq());

        List<AttackEffectDTO> attackEffects = new ArrayList<>();
        if (action.getAttack() != null && action.getAttack().getEffects() != null) {
            attackEffects.addAll(action.getAttack().getEffects());
            action.getAttack().getEffects().clear();
        }
        entityToUpdate.setAttack(action.getAttack());

        List<MovementEffectDTO> movementEffects = new ArrayList<>();
        if (action.getMovement() != null && action.getMovement().getEffects() != null) {
            movementEffects.addAll(action.getMovement().getEffects());
            action.getMovement().getEffects().clear();
        }
        entityToUpdate.setMovement(action.getMovement());

        List<SkillEffectDTO> skillEffectDTOS = new ArrayList<>();
        if (action.getSkill() != null && action.getSkill().getEffects() != null) {
            skillEffectDTOS.addAll(action.getSkill().getEffects());
            action.getSkill().getEffects().clear();
        }
        entityToUpdate.setSkill(action.getSkill());

        entityToUpdate.setRestoreCards(action.getRestoreCards());
        entityToUpdate.setSummonActions(null); // Ptfuj

        // Save the entity
        log.info(entityToUpdate.toString());
        entityToUpdate = actionRepo.save(entityToUpdate);

        // Movement effects
        if (entityToUpdate.getMovement() != null && entityToUpdate.getMovement().getEffects() != null) {
            for (MovementEffectDTO movementEffect : movementEffects) {
                EffectDTO effect = processEffects(movementEffect.getEffect());
                movementEffect.setKey(new MovementEffectDTO.MovementEffectId(entityToUpdate.getMovement().getId(), effect.getId()));
                movementEffect.setEffect(effect);
            }

            entityToUpdate.getMovement().getEffects().addAll(movementEffects);
        }

        // Skill effects
        if (entityToUpdate.getSkill() != null && entityToUpdate.getSkill().getEffects() != null) {
            for (SkillEffectDTO skillEffectDTO : skillEffectDTOS) {
                EffectDTO effect = processEffects(skillEffectDTO.getEffect());

                skillEffectDTO.setKey(new SkillEffectDTO.SkillEffectId(entityToUpdate.getSkill().getId(), effect.getId()));
                skillEffectDTO.setEffect(effect);
            }

            entityToUpdate.getSkill().getEffects().addAll(skillEffectDTOS);
        }

        // Attack effects
        if (entityToUpdate.getAttack() != null && entityToUpdate.getAttack().getEffects() != null) {
            for (AttackEffectDTO attackEffect : attackEffects) {
                EffectDTO effect = processEffects(attackEffect.getEffect());

                attackEffect.setKey(new AttackEffectDTO.AttackEffectId(entityToUpdate.getAttack().getId(), effect.getId()));
                attackEffect.setEffect(effect);
            }

            entityToUpdate.getAttack().getEffects().addAll(attackEffects);
        }

        ActionDTO lateSave = actionRepo.save(entityToUpdate);

        return lateSave;
    }

    /**
     * should create everything in action
     *
     * @param action
     * @return
     */
    public ActionDTO createInnner(ActionDTO action) {
        ActionDTO entityToUpdate = new ActionDTO(action);

        entityToUpdate.setAttack(null);
        entityToUpdate.setMovement(null);
        entityToUpdate.setSkill(null);
        entityToUpdate.setRestoreCards(null);
        entityToUpdate = actionRepo.save(entityToUpdate);

        ActionDTO lateSave = updateInner(entityToUpdate.getId(), action);

        return lateSave;
    }

    @PutMapping("/actions/{id}")
    @CacheEvict(value = "action", allEntries = true)
    public ResponseEntity<MessageResponse> update(@PathVariable int id, @RequestBody ActionDTO action) {
        validation.validate(action);

        ActionDTO updated = updateInner(id, action);

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Action with id '{}' updated!", id), HttpStatus.OK);
    }

    @DeleteMapping("/actions/{id}")
    @CacheEvict(value = "action", allEntries = true)
    public ResponseEntity<MessageResponse> delete(@PathVariable int id) {
        ActionDTO entity = actionRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '{}' not found!", id));

        actionRepo.delete(entity);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Action with id '{}' deleted!", id), HttpStatus.OK);
    }

    @PostMapping("/actions")
    @CacheEvict(value = "action", allEntries = true)
    public ResponseEntity<MessageResponse> createList(@RequestBody List<ActionDTO> actions) {
        List<String> ids = new ArrayList<>();

        for (ActionDTO action : actions) {
            validation.validate(action);

            ActionDTO lateSave = createInnner(action);
            ids.add(String.valueOf(lateSave.getId()));
        }

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Actions %s created!", String.join(", ", ids)),
                HttpStatus.OK);
    }

    @GetMapping("/actions/{id}/card")
    @Cacheable(value = "actionCard", key="T(java.util.Objects).hash(#id)")
    public ResponseEntity<LinkedHashMap<String, Object>> getCard(@PathVariable int id) {
        ActionDTO entitydto = actionRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '{}' not found!", id));

        Initialization.hibernateInitializeAll(entitydto);

        Action entity = Action.fromDTO(entitydto);

        enum Source {
            ENEMY,
            CLASS,
            RACE,
            UNKNOWN,
        }

        // enemies
        List<Enemy> enemies = enemyRepo.findAll()
                .stream()
                .map(Enemy::fromDTO)
                .filter((e) -> e.getActions().stream().anyMatch((a) -> a.getKey().getIdAction() == id))
                .toList();
        // classes
        List<Clazz> classes = clazzRepo.findAll()
                .stream()
                .map(Clazz::fromDTO)
                .filter((c) -> c.getActions().stream().anyMatch((a) -> a.getKey().getIdAction() == id))
                .toList();
        // races
        List<Race> races = raceRepo.findAll()
                .stream()
                .map(Race::fromDTO)
                .filter((r) -> r.getActions().stream().anyMatch((a) -> a.getKey().getIdAction() == id))
                .toList();

        // check if there is exactly one item in one of the lists and zero in the others
        Source source = null;
        String color = null;
        String icon = null;
        if (classes.size() == 1 && enemies.isEmpty() && races.isEmpty()) {
            source = Source.CLASS;

            color = switch (classes.get(0).getTitle()) {
                case "Knight" -> "#D60D00";
                case "Mage" -> "#8B00DB";
                case "Rogue" -> "#00478A";
                case "Bard" -> "#00B3B0";
                default -> "#000000";
            };

            icon = classes.get(0).getUrl().replace(".png", "_nobg.png");
        } else if (races.size() == 1 && enemies.isEmpty() && classes.isEmpty()) {
            source = Source.RACE;

            color = switch (races.get(0).getTitle()) {
                case "Human" -> "#BD8100";
                case "Elf" -> "#1CBD00";
                case "Dwarf" -> "#C7BD00";
                case "Demonkin" -> "#7D0041";
                default -> "#000000";
            };

            icon = races.get(0).getUrl().replace(".png", "_nobg.png");
        } else if (!enemies.isEmpty() && classes.isEmpty() && races.isEmpty()) {
            source = Source.ENEMY;
            color = "#000000";
        } else {
            source = Source.UNKNOWN;
            color = "#000000";
        }

        log.info(entity.toString());

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = "";
        try {
            json = ow.writeValueAsString(entity);
        } catch (Exception e) {
            log.error("Error while converting to json", e);
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }

        LinkedHashMap<String, Object> map = new Gson().fromJson(json, LinkedHashMap.class);
        map.put("source", source.toString());
        map.put("color", color);
        map.put("icon", icon);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @Autowired
    public void setRepository(ActionRepo repository) {
        this.actionRepo = repository;
    }

    @Autowired
    public void setEffectRepo(EffectRepo effectRepo) {
        this.effectRepo = effectRepo;
    }

    @Autowired
    public void setValidation(ValidationService validation) {
        this.validation = validation;
    }
}
