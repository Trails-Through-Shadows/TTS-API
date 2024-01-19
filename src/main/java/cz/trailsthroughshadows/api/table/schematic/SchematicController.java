package cz.trailsthroughshadows.api.table.schematic;

import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.Pagination;
import cz.trailsthroughshadows.api.rest.model.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.RestResponse;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.rest.model.error.type.MessageError;
import cz.trailsthroughshadows.api.table.schematic.location.Location;
import cz.trailsthroughshadows.api.table.schematic.location.LocationRepo;
import cz.trailsthroughshadows.api.table.schematic.obstacle.Obstacle;
import cz.trailsthroughshadows.api.table.schematic.obstacle.ObstacleRepo;
import cz.trailsthroughshadows.api.table.schematic.part.Part;
import cz.trailsthroughshadows.api.table.schematic.part.PartRepo;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Component
@Cacheable(value = "schematic")
@RestController(value = "Schematic")
public class SchematicController {
    private PartRepo partRepo;
    private LocationRepo locationRepo;
    private ObstacleRepo obstacleRepo;

    @GetMapping("/parts")
    public ResponseEntity<RestPaginatedResult<Part>> getParts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter, // TODO: Re-Implement filtering
            @RequestParam(defaultValue = "") String sort // TODO: Re-Implement sorting
    ) {
        List<Part> entries = partRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .toList();

        List<Part> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage), HttpStatus.OK);
    }

    @GetMapping("/parts/{id}")
    public ResponseEntity<Part> getPartById(@PathVariable int id) {
        Part part = partRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Part with id '%d' not found!", id));

        return new ResponseEntity<>(part, HttpStatus.OK);
    }

    @DeleteMapping("/parts/{id}")
    @CacheEvict(value = "schematic", allEntries = true)
    public ResponseEntity<RestResponse> deletePartById(@PathVariable int id) {
        Part part = partRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Part with id '%d' not found!", id));

        partRepo.delete(part);
        return new ResponseEntity<>(RestResponse.of("Part deleted!"), HttpStatus.OK);
    }

    @PostMapping("/parts")
    public ResponseEntity<RestResponse> createParts(@RequestBody List<Part> parts) {
        List<Integer> conflicts = parts.stream()
                .filter(part -> part.getId() != null && partRepo.existsById(part.getId()))
                .map(Part::getId)
                .toList();

        if (!conflicts.isEmpty()) {
            RestError error = new RestError(HttpStatus.CONFLICT, "Parts already exists!");

            for (Integer conflict : conflicts) {
                error.addSubError(new MessageError("Part with id '%d' already exists!", conflict));
            }
            throw new RestException(error);
        }

        // TODO: FIX IT, its creating new IDs instead of using ID that was provided
        partRepo.saveAll(parts);
        return new ResponseEntity<>(RestResponse.of("Parts created!"), HttpStatus.OK);
    }

    @GetMapping("/locations")
    public ResponseEntity<RestPaginatedResult<Location>> getLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter, // TODO: Implement filtering
            @RequestParam(defaultValue = "id:dsc") String sort // TODO: Implement sorting
    ) {
        List<Location> entries = locationRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .toList();

        List<Location> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage), HttpStatus.OK);
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<?> getLocationById(@PathVariable int id) {
        Location location = locationRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Location with id '%d' not found!", id));

        return new ResponseEntity<>(location, HttpStatus.OK);
    }

    @GetMapping("/obstacles")
    public ResponseEntity<?> getObstacles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter, // TODO: Implement filtering
            @RequestParam(defaultValue = "id:dsc") String sort // TODO: Implement sorting)
    ) {
        List<Obstacle> entries = obstacleRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .toList();

        List<Obstacle> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage), HttpStatus.OK);
    }

    @GetMapping("/obstacles/{id}")
    public ResponseEntity<?> getObstacleById(@PathVariable int id) {
        Obstacle obstacle = obstacleRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Obstacle with id '%d' not found!", id));

        return new ResponseEntity<>(obstacle, HttpStatus.OK);
    }

    /**
     * ===============================================
     */

    @Autowired
    public void setPartRepo(PartRepo partRepo) {
        this.partRepo = partRepo;
    }

    @Autowired
    public void setLocationRepo(LocationRepo locationRepo) {
        this.locationRepo = locationRepo;
    }

    @Autowired
    public void setObstacleRepo(ObstacleRepo obstacleRepo) {
        this.obstacleRepo = obstacleRepo;
    }
}
