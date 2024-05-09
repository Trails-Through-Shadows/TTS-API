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

    @Operation(
            summary = "Get all classes",
            description = """
                    # Get all classes
                    Retrieves all class records with support for advanced query capabilities such as pagination, filtering, sorting, and selective field loading. By default, it employs lazy loading of items.

                    **Parameters**:
                    - `page` - Specifies the page number, starting from 0.
                    - `limit` - Number of items per page, default is 100.
                    - `filter` - Defines the conditions for filtering the classes. Supported operations include eq, of, is, gt, gte, lt, lte, has, and bwn.
                    - `sort` - Defines the order of the results. Format example: &sort=name:asc,complexity:desc.
                    - `include` - Specifies which fields to load; if empty, all fields are considered.
                    - `lazy` - Determines if only specified fields should be loaded (true) or all fields (false).

                    These parameters allow for detailed customization of the returned data, accommodating various user needs for data retrieval and display.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All classes retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestPaginatedResult.class))}),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/classes")
    @Cacheable(value = "class", key = "T(java.util.Objects).hash(#page, #limit, #filter, #sort, #include, #lazy)")
    public ResponseEntity<RestPaginatedResult<Clazz>> getClasses(
            @Parameter(description = "Page number, starts from 0. Helps in paginating the result set.", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page. Determines the size of each page of results.", required = false)
            @RequestParam(defaultValue = "100") int limit,
            @Parameter(description = "Filter conditions in the format: &filter=name:eq:Warrior,complexity:lte:5,... Supported operations include: eq, of, is, gt, gte, lt, lte, has, bwn (between, numbers are split by _).", required = false)
            @RequestParam(defaultValue = "") String filter,
            @Parameter(description = "Sorting parameters in the format: &sort=name:asc,complexity:desc,... Controls the order in which classes are returned.", required = false)
            @RequestParam(defaultValue = "") String sort,
            @Parameter(description = "Specifies the fields to be loaded, which is case sensitive. If left empty, all fields are loaded.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **true** loads only specified fields in 'include', **false** loads all fields.", required = false)
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

    @Operation(
            summary = "Get Class by ID",
            description = """
                    # Get Class by ID
                    Retrieves detailed information about a specific class using its unique identifier. This endpoint supports selective field loading through optional parameters, allowing for optimized data retrieval tailored to specific requirements.

                    **Parameters**:
                    - `id` - The unique identifier of the class to be retrieved. This is required and cannot be empty.
                    - `include` - Optional. Specifies the case-sensitive fields to be loaded. If left empty, all fields are loaded.
                    - `lazy` - Optional. Controls the loading of fields: if set to **true**, only fields specified in 'include' are loaded; if **false** or omitted, all fields are loaded.

                    This method is designed to efficiently retrieve class data while allowing customization of the returned data set.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Clazz.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Class not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/classes/{id}")
    @Cacheable(value = "class", key = "T(java.util.Objects).hash(#id, #include, #lazy)")
    public ResponseEntity<Clazz> findById(
            @Parameter(description = "The unique identifier of the class to be retrieved. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "Specifies the case-sensitive fields to be loaded. Leave empty to load all fields.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **false** - All fields are loaded; **true** - Only specified fields in 'include' are loaded.", required = false)
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        ClazzDTO entity = clazzRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Class with id '{}' not found! ", id));

        if (lazy && !include.isEmpty()) {
            Initialization.hibernateInitializeAll(entity, include);
        } else if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        }

        return new ResponseEntity<>(Clazz.fromDTO(entity), HttpStatus.OK);
    }

    @Operation(
            summary = "Update an existing class",
            description = """
                    # Update an existing class
                    Updates a class entity using its unique identifier with the provided class details. This operation requires:
                    - `id` - The unique identifier of the class to be updated. It must be provided as a path variable.
                    - `entity` - The updated details of the class, provided within the request body.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class successfully updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or bad request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Class not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @PutMapping("classes/{id}")
    @CacheEvict(value = "class", allEntries = true)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> updateEntity(
            @Parameter(description = "The unique identifier of the class to be updated. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "The class data to be used for the update. Cannot be null or empty.", required = true)
            @RequestBody ClazzDTO entity
    ) {
        log.debug("Updating class with id: " + id);

        // Validate class
        validation.validate(entity);

        ClazzDTO clazzToUpdate = clazzRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Class with id '{}' not found! ", id));

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
                        .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '{}' not found! ", action.getKey().getIdAction()));

                action.setKey(new ClazzAction.ClazzActionId(clazzToUpdate.getId(), actionDTO.getId()));
                action.setAction(actionDTO);
            }

            clazzToUpdate.getActions().clear();
            clazzToUpdate.getActions().addAll(entityActions);
        }

        if (!entityEffects.isEmpty() || !entityActions.isEmpty()) {
            clazzRepo.save(clazzToUpdate);
        }

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Class with id '{}' updated!", id), HttpStatus.OK);
    }

    @Operation(
            summary = "Create multiple classes",
            description = """
                    # Create multiple classes
                    This endpoint allows for the batch creation of multiple class entities at once. Clients must provide a list of class details in the request body.

                    **Parameters**:
                    - `clazzes` - List of class details; each must conform to the ClazzDTO specification for successful creation.

                    This method is particularly useful for initializing class data or conducting bulk imports, offering an efficient way to handle multiple class records simultaneously.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All classes created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data in request body",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @PostMapping("/classes")
    @CacheEvict(value = "class", allEntries = true)
    public ResponseEntity<MessageResponse> createEntity(
            @Parameter(description = "List of class data to be created. Each entry must conform to the ClazzDTO structure and include all necessary details as required by the system.", required = true)
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

    @Operation(
            summary = "Delete a class",
            description = """
                    # Delete a class
                    This endpoint allows for the deletion of a class specified by its unique identifier. The operation checks if the class exists within the system and then deletes it, thereby permanently removing it from the database.

                    **Parameters**:
                    - `id` - The unique identifier of the class to be deleted. This ID must be specified in the path to locate and delete the class.

                    It is necessary to verify user authorization before performing this action to ensure that only qualified users can delete classes.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class deleted successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or request parameters",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User does not have permission to delete the class",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Class not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error occurred",
                    content = @Content)
    })
    @DeleteMapping("/classes/{id}")
    @CacheEvict(value = "class", allEntries = true)
    public ResponseEntity<MessageResponse> deleteEntity(
            @Parameter(description = "The unique identifier of the class to be deleted. Cannot be empty.", required = true)
            @PathVariable int id
    ) {
        ClazzDTO entity = clazzRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Class with id '{}' not found! ", id));

        clazzRepo.delete(entity);

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Class with id '{}' deleted!", id), HttpStatus.OK);
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
