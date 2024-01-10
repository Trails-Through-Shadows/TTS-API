package cz.trailsthroughshadows.api.table.schematic;

import cz.trailsthroughshadows.api.rest.Pagination;
import cz.trailsthroughshadows.api.rest.Response;
import cz.trailsthroughshadows.api.table.schematic.location.Location;
import cz.trailsthroughshadows.api.table.schematic.location.LocationRepo;
import cz.trailsthroughshadows.api.table.schematic.obstacle.Obstacle;
import cz.trailsthroughshadows.api.table.schematic.obstacle.ObstacleRepo;
import cz.trailsthroughshadows.api.table.schematic.part.Part;
import cz.trailsthroughshadows.api.table.schematic.part.PartRepo;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@Component
@Cacheable(value = "schematic")
@RestController(value = "Schematic")
public class SchematicController {
    private PartRepo partRepo;
    private LocationRepo locationRepo;
    private ObstacleRepo obstacleRepo;

    @GetMapping("/parts")
    public ResponseEntity<?> getParts(
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
        return Response.Status.OK.getResult(pagination, entriesPage);
    }

    @GetMapping("/part/{id}")
    public ResponseEntity<?> getPartById(@PathVariable int id) {
        Part part = partRepo.findById(id).orElse(null);

        if (part == null) {
            String message = "Part with id '" + id + "' not found!";
            return Response.Status.NOT_FOUND.getErrorCode(message);
        }

        return Response.Status.OK.getResult(part);
    }

    @DeleteMapping("/part/{id}")
    @CacheEvict(value = "schematic", allEntries = true)
    public ResponseEntity<?> deletePartById(@PathVariable int id) {
        Part part = partRepo.findById(id).orElse(null);

        if (part == null) {
            String message = "Part with id '" + id + "' not found!";
            return Response.Status.NOT_FOUND.getErrorCode(message);
        }

        partRepo.delete(part);
        String message = "Part with id '" + id + "' deleted!";
        return Response.Status.NO_CONTENT.getResult(message);
    }

    @PostMapping("/part")
    public ResponseEntity<?> createPart(@RequestBody Part part) {
        if (partRepo.findById(part.getId()).isPresent()) {
            String message = "Part with id '" + part.getId() + "' already exists!";
            return Response.Status.IM_A_TEAPOT.getErrorCode(message);
        }

        partRepo.save(part);
        String message = "Part with id '" + part.getId() + "' created!";

        return Response.Status.OK.getResult(message);
    }

    @GetMapping("/locations")
    public ResponseEntity<?> getLocations(
            @RequestParam(defaultValue = "1") int page,
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
        return Response.Status.OK.getResult(pagination, entriesPage);
    }

    @GetMapping("/location/{id}")
    public ResponseEntity<?> getLocationById(@PathVariable int id) {
        Location location = locationRepo.findById(id).orElse(null);

        if (location == null) {
            String message = "Location with id '" + id + "' not found!";
            return Response.Status.NOT_FOUND.getErrorCode(message);
        }

        return Response.Status.OK.getResult(location);
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
        return Response.Status.OK.getResult(pagination, entriesPage);
    }

    @GetMapping("/obstacle/{id}")
    public ResponseEntity<?> getObstacleById(@PathVariable int id) {
        Obstacle obstacle = obstacleRepo.findById(id).orElse(null);

        if (obstacle == null) {
            String message = "Obstacle with id '" + id + "' not found!";
            return Response.Status.NOT_FOUND.getErrorCode(message);
        }

        return Response.Status.OK.getResult(obstacle);
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
    public void setObstacleRepo(ObstacleRepo obstacleRepo) {this.obstacleRepo = obstacleRepo;    }
}
