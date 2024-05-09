package cz.trailsthroughshadows.api.table.enemy;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.action.ActionRepo;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.effect.EffectRepo;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.forothers.EnemyEffectDTO;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.enemy.model.dto.EnemyActionDTO;
import cz.trailsthroughshadows.api.table.enemy.model.dto.EnemyDTO;
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
@RestController(value = "Enemy")
public class EnemyController {

    private ValidationService validation;
    private EnemyRepo enemyRepo;
    private EffectRepo effectRepo;
    private ActionRepo actionRepo;

    @Operation(
            summary = "Get all enemies",
            description = """
                    # Get all enemies
                    This endpoint retrieves all enemy entities with support for advanced query capabilities such as pagination, filtering, sorting, and selective field loading. By default, it employs lazy loading of items.

                    **Parameters**:
                    - `page` - Specifies the page number, starting from 0.
                    - `limit` - Number of items per page, default is 100.
                    - `filter` - Defines the conditions for filtering the enemies. Supported operations include eq, of, is, gt, gte, lt, lte, has, and bwn.
                    - `sort` - Defines the order of the results. Format example: &sort=name:asc,strength:desc.
                    - `include` - Specifies which fields to load; if empty, all fields are considered.
                    - `lazy` - Determines if only specified fields should be loaded (true) or all fields (false).

                    These parameters allow for detailed customization of the returned data, accommodating various user needs for data retrieval and display.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All enemies retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestPaginatedResult.class))}),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/enemies")
    @Cacheable(value = "enemy", key = "T(java.util.Objects).hash(#page, #limit, #filter, #sort, #include, #lazy)")
    public ResponseEntity<RestPaginatedResult<Enemy>> getEnemies(
            @Parameter(description = "Page number, starts from 0. Helps in paginating the result set.", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page. Determines the size of each page of results.", required = false)
            @RequestParam(defaultValue = "100") int limit,
            @Parameter(description = "Filter conditions in the format: &filter=name:eq:Goblin,type:has:undead,... Supported operations include: eq, of, is, gt, gte, lt, lte, has, bwn (between, numbers are split by _).", required = false)
            @RequestParam(defaultValue = "") String filter,
            @Parameter(description = "Sorting parameters in the format: &sort=strength:asc,speed:desc,... Controls the order in which enemies are returned.", required = false)
            @RequestParam(defaultValue = "") String sort,
            @Parameter(description = "Specifies the fields to be loaded, which is case sensitive. If left empty, all fields are loaded.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **true** loads only specified fields in 'include', **false** loads all fields.", required = false)
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<EnemyDTO> entries = enemyRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<EnemyDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit),
                entries.size(), page, limit);
        return new ResponseEntity<>(
                RestPaginatedResult.of(pagination, entriesPage.stream().map(Enemy::fromDTO).toList()), HttpStatus.OK);
    }

    @Operation(
            summary = "Get Enemy by ID",
            description = """
                    # Get Enemy by ID
                    Retrieves detailed information about a specific enemy using its unique identifier. This endpoint supports selective field loading through optional parameters, allowing for optimized data retrieval tailored to specific needs.

                    **Parameters**:
                    - `id` - The unique identifier of the enemy to be retrieved. This is required and cannot be empty.
                    - `include` - Optional. Specifies the case-sensitive fields to be loaded. If left empty, all fields are loaded.
                    - `lazy` - Optional. Controls the loading of fields: if set to **true**, only fields specified in 'include' are loaded; if **false** or omitted, all fields are loaded.

                    This method is designed to efficiently retrieve detailed data on individual enemies, providing flexibility in data retrieval and reducing overhead for systems and applications.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enemy successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Enemy.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Enemy not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/enemies/{id}")
    @Cacheable(value = "enemy", key = "T(java.util.Objects).hash(#id, #include, #lazy)")
    public ResponseEntity<Enemy> findById(
            @Parameter(description = "The unique identifier of the enemy to be retrieved. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "Specifies the case-sensitive fields to be loaded. Leave empty to load all fields.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **false** - All fields are loaded; **true** - Only specified fields in 'include' are loaded.", required = false)
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        EnemyDTO entity = enemyRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Enemy with id '{}' not found!", id));

        if (lazy && !include.isEmpty()) {
            Initialization.hibernateInitializeAll(entity, include);
        } else if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        }

        return new ResponseEntity<>(Enemy.fromDTO(entity), HttpStatus.OK);
    }

    @Operation(
            summary = "Update an existing enemy",
            description = """
                    # Update an existing enemy
                    Updates an enemy entity using its unique identifier with the provided enemy details. This operation requires:
                    - `id` - The unique identifier of the enemy to be updated. It must be provided as a path variable.
                    - `enemy` - The updated details of the enemy, provided within the request body.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enemy successfully updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or bad request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Enemy not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @PutMapping("/enemies/{id}")
    @CacheEvict(value = "enemy", allEntries = true)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> updateEnemyById(
            @Parameter(description = "The unique identifier of the enemy to be updated. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "The enemy data to be used for the update. Cannot be null or empty.", required = true)
            @RequestBody EnemyDTO enemy
    ) {
        log.debug("Updating enemy with id: " + id);

        // Validate enemy
        validation.validate(enemy);

        EnemyDTO enemyToUpdate = enemyRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Enemy with id %d not found", id));

        // Remove relations and save them for later
        List<EnemyEffectDTO> enemyEffects = new ArrayList<>();
        if (enemy.getEffects() != null) {
            enemyEffects.addAll(enemy.getEffects());
            enemy.getEffects().clear();
        }

        List<EnemyActionDTO> enemyActions = new ArrayList<>();
        if (enemy.getActions() != null) {
            enemyActions.addAll(enemy.getActions());
            enemy.getActions().clear();
        }

        // Update enemy
        enemyToUpdate.setTag(enemy.getTag());
        enemyToUpdate.setTitle(enemy.getTitle());
        enemyToUpdate.setDescription(enemy.getDescription());
        enemyToUpdate.setBaseDefence(enemy.getBaseDefence());
        enemyToUpdate.setBaseHealth(enemy.getBaseHealth());
        enemyToUpdate.setBaseInitiative(enemy.getBaseInitiative());
        enemyToUpdate = enemyRepo.save(enemyToUpdate);

        // Post load relations
        if (enemyToUpdate.getEffects() != null) {
            for (EnemyEffectDTO effect : enemyEffects) {
                EffectDTO effectDTO = processEffects(effect.getEffect());

                effect.setKey(new EnemyEffectDTO.EnemyEffect(enemyToUpdate.getId(), effectDTO.getId()));
                effect.setEffect(effectDTO);
            }

            enemyToUpdate.getEffects().clear();
            enemyToUpdate.getEffects().addAll(enemyEffects);
        }

        if (enemyToUpdate.getActions() != null) {
            for (EnemyActionDTO action : enemyActions) {
                ActionDTO actionDTO = actionRepo.findById(action.getAction().getId())
                        .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id %d not found", action.getAction().getId()));

                action.setKey(new EnemyActionDTO.EnemyActionId(enemyToUpdate.getId(), actionDTO.getId()));
                action.setAction(actionDTO);
            }

            enemyToUpdate.getActions().clear();
            enemyToUpdate.getActions().addAll(enemyActions);
        }

        if (!enemyActions.isEmpty() || !enemyEffects.isEmpty()) {
            enemyRepo.save(enemyToUpdate);
        }

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Enemy with id '{}' updated!", id), HttpStatus.OK);
    }

    @Operation(
            summary = "Create multiple enemies",
            description = """
                    # Create multiple enemies
                    This endpoint allows for the batch creation of multiple enemies at once. Clients must provide a list of enemy details in the request body.

                    **Parameters**:
                    - `enemies` - List of enemy details; each entry must conform to the EnemyDTO specification for successful creation.

                    This method is particularly useful for populating game worlds or scenarios with multiple enemies, offering an efficient way to handle bulk data management.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All enemies created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data in request body",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @PostMapping("/enemies")
    @CacheEvict(value = "enemy", allEntries = true)
    public ResponseEntity<MessageResponse> createEnemies(
            @Parameter(description = "List of enemy data to be created. Each entry must conform to the EnemyDTO structure and include all necessary details as required by the system.", required = true)
            @RequestBody List<EnemyDTO> enemies
    ) {
        // Validate enemies
        enemies.forEach(validation::validate);

        // Remove ids to prevent conflicts
        enemies.forEach(e -> e.setId(null));

        // Remove relations and save them for later
        Map<String, Pair<List<EnemyEffectDTO>, List<EnemyActionDTO>>> enemyRelations = new HashMap<>();
        enemies.forEach(enemy -> {
            enemyRelations.put(enemy.getTag(), new Pair<>(new ArrayList<>(enemy.getEffects()), new ArrayList<>(enemy.getActions())));
            enemy.setEffects(null);
            enemy.setActions(null);
        });

        // Save enemies
        enemies = enemyRepo.saveAll(enemies);

        // Load relations
        enemies.forEach(enemy -> {
            Pair<List<EnemyEffectDTO>, List<EnemyActionDTO>> relations = enemyRelations.get(enemy.getTag());

            enemy.setEffects(new ArrayList<>(relations.first()));
            enemy.getEffects().forEach(effect -> effect.getKey().setIdEnemy(enemy.getId()));

            enemy.setActions(new ArrayList<>(relations.second()));
            enemy.getActions().forEach(action -> action.getKey().setIdEnemy(enemy.getId()));
        });

        // Save enemies relations
        enemies = enemyRepo.saveAll(enemies);

        String ids = enemies.stream().map((entry) -> String.valueOf(entry.getId())).toList().toString();
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Enemies with ids '%s' created!", ids), HttpStatus.OK);
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
            summary = "Delete an enemy",
            description = """
                    # Delete an enemy
                    This endpoint allows for the deletion of an enemy specified by its unique identifier. After verifying that the enemy exists within the system, it proceeds to remove the enemy permanently from the database.

                    **Parameters**:
                    - `id` - The unique identifier of the enemy to be deleted. This ID is crucial for locating the enemy in the system to ensure accurate deletion.

                    The operation also includes an authorization check to confirm that only users with appropriate permissions can execute deletions of enemy entities.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enemy deleted successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID or request parameters",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User lacks permission to delete the enemy",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Enemy not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error occurred",
                    content = @Content)
    })
    @DeleteMapping("/enemies/{id}")
    @CacheEvict(value = "enemy", allEntries = true)
    public ResponseEntity<MessageResponse> deleteEnemy(
            @Parameter(description = "The unique identifier of the enemy to be deleted. Cannot be empty.", required = true)
            @PathVariable int id) {

        EnemyDTO enemyDTO = enemyRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Enemy with id %d not found", id));

        enemyRepo.delete(enemyDTO);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Enemy with id '{}' deleted!", id),
                HttpStatus.OK);
    }

    @Autowired
    public void setRepository(EnemyRepo repository) {
        this.enemyRepo = repository;
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