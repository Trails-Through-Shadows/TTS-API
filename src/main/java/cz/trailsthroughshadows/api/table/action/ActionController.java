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


    @Operation(
            summary = "Get all actions",
            description = """
                    # Get all actions
                    This endpoint retrieves all actions with support for advanced query capabilities such as pagination, filtering, sorting, and selective field loading. By default, it employs lazy loading of items.

                    **Parameters**:
                    - `page` - Specifies the page number, starting from 0.
                    - `limit` - Number of items per page, default is 100.
                    - `filter` - Defines the conditions for filtering the actions. Supported operations include eq, of, is, gt, gte, lt, lte, has, and bwn.
                    - `sort` - Defines the order of the results. Format example: &sort=id:asc,title:desc.
                    - `include` - Specifies which fields to load; if empty, all fields are considered.
                    - `lazy` - Determines if only specified fields should be loaded (true) or all fields (false).

                    These parameters allow for detailed customization of the returned data, accommodating various user needs for data retrieval and display.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All actions retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestPaginatedResult.class))}),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/actions")
    @Cacheable(value = "action", key = "T(java.util.Objects).hash(#page, #limit, #filter, #sort, #include, #lazy)")
    public ResponseEntity<RestPaginatedResult<Action>> findAllEntities(
            @Parameter(description = "Page number, starts from 0. Helps in paginating the result set.", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page. Determines the size of each page of results.", required = false)
            @RequestParam(defaultValue = "100") int limit,
            @Parameter(description = "Filter conditions in the format: &filter=title:eq:fireball,id:bwn:1_20,type:is:false,... Supported operations include: eq, of, is, gt, gte, lt, lte, has, bwn (between, numbers are split by _).", required = false)
            @RequestParam(defaultValue = "") String filter,
            @Parameter(description = "Sorting parameters in the format: &sort=id:asc,title:desc,... Controls the order in which actions are returned.", required = false)
            @RequestParam(defaultValue = "") String sort,
            @Parameter(description = "Specifies the fields to be loaded, which is case sensitive. If left empty, all fields are loaded.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **true** loads only specified fields in 'include', **false** loads all fields.", required = false)
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
            summary = "Get an action by its id",
            description = """
                    # Get an action by its id
                    Retrieves a specific action by its unique identifier. By default, it loads all fields and returns them unless specified otherwise through query parameters.

                    **Parameters**:
                    - `id` - The unique identifier of the action to be retrieved. This is required and cannot be empty.
                    - `include` - Optional. Specifies the case-sensitive fields to be loaded. If left empty, all fields are loaded.
                    - `lazy` - Optional. Controls the loading of fields: if set to **true**, only fields specified in 'include' are loaded; if **false** or omitted, all fields are loaded.

                    The Action model includes:
                    - **attack**: Contains effects
                    - **movement**: Contains effects
                    - **skill**: Contains effects
                    - **restoreCards**: Does not contain effects
                    - **summonActions**: Contains effects
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Action successfully found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Action.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Action not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/actions/{id}")
    @Cacheable(value = "action", key = "T(java.util.Objects).hash(#id, #include, #lazy)")
    public ResponseEntity<Action> findById(
            @Parameter(description = "The unique identifier of the action to be retrieved. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "Specifies the case-sensitive fields to be loaded. Leave empty to load all fields.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **false** - All fields are loaded; **true** - Only specified fields in 'include' are loaded.", required = false)
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

    @Operation(
            summary = "Update an existing action",
            description = """
                    # Update an existing action
                    Updates an action using its unique identifier with the provided action details. This operation requires:
                    - `id` - The unique identifier of the action to be updated. It must be provided as a path variable.
                    - `action` - The updated details of the action, provided within the request body.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Action successfully updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or bad request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Action not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @PutMapping("/actions/{id}")
    @CacheEvict(value = "action", allEntries = true)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> update(
            @Parameter(description = "The unique identifier of the action to be updated. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "The action data to be used for the update. Cannot be null or empty.", required = true)
            @RequestBody ActionDTO action
    ) {

        validation.validate(action);
        ActionDTO updated = updateInner(id, action);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Action with id '{}' updated!", id), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete an action",
            description = """
                    # Delete an action
                    This endpoint allows for the deletion of an action specified by its unique identifier. It checks if the action exists and then proceeds to delete it, permanently removing it from the system.

                    **Parameters**:
                    - `id` - The unique identifier of the action to be deleted. It must be provided in the path to execute the deletion.

                    Users must be authorized to perform this operation, ensuring that only eligible users can delete actions.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Action deleted successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or request parameters",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Action not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @DeleteMapping("/actions/{id}")
    @CacheEvict(value = "action", allEntries = true)
    public ResponseEntity<MessageResponse> delete(
            @Parameter(description = "The unique identifier of the action to be deleted. Cannot be empty.", required = true)
            @PathVariable int id
    ) {

        ActionDTO entity = actionRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '{}' not found!", id));

        actionRepo.delete(entity);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Action with id '{}' deleted!", id), HttpStatus.OK);
    }

    @Operation(
            summary = "Create multiple actions",
            description = """
                    # Create multiple actions
                    This endpoint allows for the batch creation of multiple actions at once. Clients must provide a list of action data in the request body.

                    **Parameters**:
                    - `actions` - List of action details; each must conform to the ActionDTO specification for successful creation.

                    This method is particularly useful for initializing data or bulk imports, offering an efficient way to handle multiple records simultaneously.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All actions created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data in request body",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @PostMapping("/actions")
    @CacheEvict(value = "action", allEntries = true)
    public ResponseEntity<MessageResponse> createList(
            @Parameter(description = "List of action data to be created. Each entry must conform to the ActionDTO structure and include all necessary details as required by the system.", required = true)
            @RequestBody List<ActionDTO> actions
    ) {

        List<String> ids = new ArrayList<>();

        for (ActionDTO action : actions) {
            validation.validate(action);

            ActionDTO lateSave = createInnner(action);
            ids.add(String.valueOf(lateSave.getId()));
        }

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Actions %s created!", String.join(", ", ids)),
                HttpStatus.OK);
    }

    @Operation(
            summary = "Get Action Card Details",
            description = """
                    # Get Action Card Details by Action ID
                    Retrieves details necessary for creating a card representation of a specific action using its unique identifier. This includes data related to the source of the action (like enemy, class, or race), along with specific visual representations such as color and icon based on the source.

                    **Parameters**:
                    - `id` - The unique identifier of the action whose card details are to be retrieved. This is required and cannot be empty.

                    This endpoint will determine the source of the action (whether it's associated with an enemy, a class, or a race) and will return specific styling attributes such as color and icon.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Action card details successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LinkedHashMap.class))}),
            @ApiResponse(responseCode = "404", description = "Action not found",
                    content = @Content),
            @ApiResponse(responseCode = "418", description = "Error while converting to JSON",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/actions/{id}/card")
    @Cacheable(value = "actionCard", key = "T(java.util.Objects).hash(#id)")
    public ResponseEntity<LinkedHashMap<String, Object>> getCard(
            @Parameter(description = "The unique identifier of the action to be retrieved. Cannot be empty.", required = true)
            @PathVariable int id
    ) {

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
