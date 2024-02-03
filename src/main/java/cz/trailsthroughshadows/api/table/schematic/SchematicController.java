package cz.trailsthroughshadows.api.table.schematic;

import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.Pagination;
import cz.trailsthroughshadows.api.rest.model.RestPaginatedResult;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.ObstacleDTO;
import cz.trailsthroughshadows.api.table.schematic.obstacle.ObstacleRepo;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Component
@Cacheable(value = "schematic")
@RestController(value = "Schematic")
public class SchematicController {
    private ObstacleRepo obstacleRepo;

    @GetMapping("/obstacles")
    public ResponseEntity<?> getObstacles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter, // TODO: Implement filtering
            @RequestParam(defaultValue = "id:dsc") String sort // TODO: Implement sorting)
    ) {
        List<ObstacleDTO> entries = obstacleRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<ObstacleDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage), HttpStatus.OK);
    }

    @GetMapping("/obstacles/{id}")
    public ResponseEntity<?> getObstacleById(@PathVariable int id) {
        ObstacleDTO obstacle = obstacleRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Obstacle with id '%d' not found!", id));

        return new ResponseEntity<>(obstacle, HttpStatus.OK);
    }

    /**
     * ===============================================
     */

    @Autowired
    public void setObstacleRepo(ObstacleRepo obstacleRepo) {
        this.obstacleRepo = obstacleRepo;
    }
}
