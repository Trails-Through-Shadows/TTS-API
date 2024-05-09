package cz.trailsthroughshadows.api.table.schematic.obstacle;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.effect.EffectRepo;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.forothers.ObstacleEffectDTO;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.ObstacleDTO;
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
@RestController(value = "Obstacle")
public class ObstacleController {

    private ValidationService validation;
    private ObstacleRepo obstacleRepo;
    private EffectRepo effectRepo;

    @Operation(
            summary = "Get all obstacles",
            description = """
                    # Get all obstacles
                    This endpoint retrieves all obstacle records with support for advanced query capabilities such as pagination, filtering, sorting, and selective field loading. By default, it employs lazy loading of items.

                    **Parameters**:
                    - `page` - Specifies the page number, starting from 0.
                    - `limit` - Number of obstacles per page, default is 100.
                    - `filter` - Defines the conditions for filtering the obstacles. Supported operations include eq, of, is, gt, gte, lt, lte, has, and bwn.
                    - `sort` - Defines the order of the results. Format example: &sort=size:asc,difficulty:desc.
                    - `include` - Specifies which fields to load; if empty, all fields are considered.
                    - `lazy` - Determines if only specified fields should be loaded (true) or all fields (false).

                    These parameters allow for detailed customization of the returned data, accommodating various user needs for data retrieval and display.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All obstacles retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestPaginatedResult.class))}),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/obstacles")
    @Cacheable(value = "obstacle", key = "T(java.util.Objects).hash(#page, #limit, #filter, #sort, #include, #lazy)")
    public ResponseEntity<RestPaginatedResult<Obstacle>> findAllObstacles(
            @Parameter(description = "Page number, starts from 0. Helps in paginating the result set.", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of obstacles per page. Determines the size of each page of results.", required = false)
            @RequestParam(defaultValue = "100") int limit,
            @Parameter(description = "Filter conditions in the format: &filter=type:eq:Natural,height:gte:10,... Supported operations include: eq, of, is, gt, gte, lt, lte, has, bwn (between, numbers are split by _).", required = false)
            @RequestParam(defaultValue = "") String filter,
            @Parameter(description = "Sorting parameters in the format: &sort=difficulty:asc,size:desc,... Controls the order in which obstacles are returned.", required = false)
            @RequestParam(defaultValue = "") String sort,
            @Parameter(description = "Specifies the fields to be loaded, which is case sensitive. If left empty, all fields are loaded.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **true** loads only specified fields in 'include', **false** loads all fields.", required = false)
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<ObstacleDTO> entries = obstacleRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<ObstacleDTO> entriesPage = entries.stream()
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
                RestPaginatedResult.of(pagination, entriesPage.stream().map(Obstacle::fromDTO).toList()),
                HttpStatus.OK);
    }

    @GetMapping("/obstacles/{id}")
    @Cacheable(value = "obstacle", key = "T(java.util.Objects).hash(#id, #include, #lazy)")
    public ResponseEntity<Obstacle> findById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        ObstacleDTO entity = obstacleRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Obstacle with id '{}' not found! " + id));

        if (lazy && !include.isEmpty()) {
            Initialization.hibernateInitializeAll(entity, include);
        } else if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        }

        return new ResponseEntity<>(Obstacle.fromDTO(entity), HttpStatus.OK);
    }

    @DeleteMapping("/obstacles/{id}")
    @CacheEvict(value = "obstacle", allEntries = true)
    public ResponseEntity<MessageResponse> deleteObstacleById(
            @PathVariable int id
    ) {
        ObstacleDTO obstacleDTO = obstacleRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Obstacle with id '{}' not found!", id));

        obstacleRepo.delete(obstacleDTO);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Obstacle with id '{}' deleted!", id), HttpStatus.OK);
    }

    @PutMapping("/obstacles/{id}")
    @CacheEvict(value = "obstacle", allEntries = true)
    public ResponseEntity<MessageResponse> updateObstacleById(
            @PathVariable int id,
            @RequestBody ObstacleDTO obstacle
    ) {
        log.debug("Updating obstacle with id: " + id);

        // Validate obstacle
        validation.validate(obstacle);

        ObstacleDTO obstacleToUpdate = obstacleRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Obstacle with id '{}' not found!", id));

        // Remove relations and save them for later
        List<ObstacleEffectDTO> obstacleEffects = new ArrayList<>();
        if (obstacle.getEffects() != null) {
            obstacleEffects.addAll(obstacle.getEffects());
            obstacle.getEffects().clear();
        }

        // Update obstacles
        obstacleToUpdate.setTag(obstacle.getTag());
        obstacleToUpdate.setTitle(obstacle.getTitle());
        obstacleToUpdate.setDescription(obstacle.getDescription());
        obstacleToUpdate.setBaseDamage(obstacle.getBaseDamage());
        obstacleToUpdate.setBaseHealth(obstacle.getBaseHealth());
        obstacleToUpdate.setCrossable(obstacle.isCrossable());
        obstacleToUpdate = obstacleRepo.save(obstacleToUpdate);

        // Post load relations
        if (obstacleToUpdate.getEffects() != null) {
            for (ObstacleEffectDTO effect : obstacleEffects) {
                EffectDTO effectDTO = processEffects(effect.getEffect());

                effect.setKey(new ObstacleEffectDTO.ObstacleEffectKey(obstacleToUpdate.getId(), effectDTO.getId()));
                effect.setEffect(effectDTO);
            }

            obstacleToUpdate.getEffects().clear();
            obstacleToUpdate.getEffects().addAll(obstacleEffects);
        }

        if (!obstacleEffects.isEmpty()) {
            obstacleRepo.save(obstacleToUpdate);
        }

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Obstacle with id '{}' updated!", id), HttpStatus.OK);
    }

    @PostMapping("/obstacles")
    @CacheEvict(value = "obstacle", allEntries = true)
    public ResponseEntity<MessageResponse> createObstacle(
            @RequestBody List<ObstacleDTO> obstacles
    ) {
        // Validate obstacle
        obstacles.forEach(validation::validate);

        // Remove ids to always create new obstacles
        obstacles.forEach(obstacle -> obstacle.setId(null));

        // Remove relations and save them for later
        Map<String, List<ObstacleEffectDTO>> obstacleRelations = new HashMap<>();
        obstacles.forEach(obstacle -> {
            obstacleRelations.put(obstacle.getTag(), new ArrayList<>(obstacle.getEffects()));
            obstacle.setEffects(null);
        });

        // Save obstacles
        obstacles = obstacleRepo.saveAll(obstacles);

        // Load relations
        obstacles.forEach(obstacle -> {
            List<ObstacleEffectDTO> relations = obstacleRelations.get(obstacle.getTag());

            obstacle.setEffects(new ArrayList<>(relations));
            obstacle.getEffects().forEach(effect -> effect.getKey().setIdObstacle(obstacle.getId()));
        });

        // Save obstacles relations
        obstacles = obstacleRepo.saveAll(obstacles);

        String ids = obstacles.stream().map((entry) -> String.valueOf(entry.getId())).toList().toString();
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Obstacles with ids '%s' created!", ids), HttpStatus.OK);
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

    /**
     * ===============================================
     */

    @Autowired
    public void setObstacleRepo(ObstacleRepo obstacleRepo) {
        this.obstacleRepo = obstacleRepo;
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
