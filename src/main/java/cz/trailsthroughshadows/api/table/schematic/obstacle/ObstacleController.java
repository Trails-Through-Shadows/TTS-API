package cz.trailsthroughshadows.api.table.schematic.obstacle;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.effect.relation.forothers.ObstacleEffectDTO;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.ObstacleDTO;
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
@RestController(value = "Obstacle")
public class ObstacleController {

    private ValidationService validation;
    private ObstacleRepo obstacleRepo;

    @GetMapping("/obstacles")
    @Cacheable(value = "obstacle")
    public ResponseEntity<RestPaginatedResult<Obstacle>> findAllEntities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "") List<String> include,
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

        if (!lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage.stream().map(Obstacle::fromDTO).toList()), HttpStatus.OK);
    }

    @GetMapping("/obstacles/{id}")
//    @Cacheable(value = "obstacle", key = "#id")
    public ResponseEntity<Obstacle> findById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        ObstacleDTO entity = obstacleRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Obstacle with id '%d' not found! " + id));

        if (!lazy && !include.isEmpty()) {
            Initialization.hibernateInitializeAll(entity, include);
        } else if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        }

        return new ResponseEntity<>(Obstacle.fromDTO(entity), HttpStatus.OK);
    }

    @DeleteMapping("/obstacles/{id}")
    @CacheEvict(value = "obstacle", key = "#id")
    public ResponseEntity<MessageResponse> deleteObstacleById(@PathVariable int id) {
        ObstacleDTO obstacleDTO = obstacleRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Obstacle with id '%d' not found!", id));

        obstacleRepo.delete(obstacleDTO);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Obstacle with id '%d' deleted!", id), HttpStatus.OK);
    }

    @PutMapping("/obstacles/{id}")
    @CacheEvict(value = "obstacle", key = "#id")
    public ResponseEntity<MessageResponse> updateObstacleById(@PathVariable int id, @RequestBody ObstacleDTO obstacle) {
        ObstacleDTO obstacleToUpdate = obstacleRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Obstacle with id '%d' not found!", id));

        // Validate obstacle
        validation.validate(obstacle);

        obstacleToUpdate.setTag(obstacle.getTag());
        obstacleToUpdate.setTitle(obstacle.getTitle());
        obstacleToUpdate.setDescription(obstacle.getDescription());
        obstacleToUpdate.setBaseDamage(obstacle.getBaseDamage());
        obstacleToUpdate.setBaseHealth(obstacle.getBaseHealth());
        obstacleToUpdate.setCrossable(obstacle.isCrossable());

        obstacleToUpdate.getEffects().retainAll(obstacle.getEffects());
        obstacleToUpdate.getEffects().addAll(obstacle.getEffects());
        obstacleToUpdate.getEffects().forEach((effect) -> effect.setIdObstacle(obstacleToUpdate.getId()));

        obstacleRepo.save(obstacleToUpdate);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Obstacle with id '%d' updated!", id), HttpStatus.OK);
    }

    @PostMapping("/obstacles")
    public ResponseEntity<MessageResponse> createObstacle(@RequestBody List<Obstacle> obstacles) {
        log.debug("Creating new obstacles: " + obstacles);

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
            obstacle.getEffects().forEach(effect -> effect.setIdObstacle(obstacle.getId()));
        });

        // Save obstacles relations
        obstacles = obstacleRepo.saveAll(obstacles);

        String ids = obstacles.stream().map((entry) -> String.valueOf(entry.getId())).toList().toString();
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Obstacles with ids '%s' created!", ids), HttpStatus.OK);
    }

    /**
     * ===============================================
     */

    @Autowired
    public void setObstacleRepo(ObstacleRepo obstacleRepo) {
        this.obstacleRepo = obstacleRepo;
    }

    @Autowired
    public void setValidation(ValidationService validation) {
        this.validation = validation;
    }
}
