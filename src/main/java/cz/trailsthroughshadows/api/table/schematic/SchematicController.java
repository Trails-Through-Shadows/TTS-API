package cz.trailsthroughshadows.api.table.schematic;

import cz.trailsthroughshadows.api.rest.Pagination;
import cz.trailsthroughshadows.api.rest.RestError;
import cz.trailsthroughshadows.api.rest.RestResult;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
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
    public RestResult getParts(
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
        return new RestResult(pagination, entriesPage);
    }

    @GetMapping("/part/{id}")
    public Object getPartById(@PathVariable int id) {
        Part part = partRepo.findById(id).orElse(null);

        if (part == null) {
            String message = "Part with id '" + id + "' not found!";
            return RestError.Type.NOT_FOUND.getErrorCode(message);
        }

        return part;
    }

    @GetMapping("/locations")
    public RestResult getLocations(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter, // TODO: Implement filtering
            @RequestParam(defaultValue = "id:dsc") String sort // TODO: Implement sorting
    ) {
        PageRequest pageRequest = PageRequest.of(Math.max(page, 0), limit);
        Page<Location> pageData = locationRepo.findAll(pageRequest);
        List<Location> entries = pageData.get()
//                .filter(entry -> {
//                    if (filter.isEmpty()) {
//                        return true;
//                    } else {
//                        return entry.getTag().toLowerCase().contains(filter.toLowerCase())
//                                || entry.getTitle().toLowerCase().contains(filter.toLowerCase())
//                                || entry.getDescription().toLowerCase().contains(filter.toLowerCase());
//                    }
//                })
                .toList();

        Pagination pagination = new Pagination(entries.size(), pageData.hasNext(), (int) pageData.getTotalElements(), pageRequest.getPageNumber(), pageRequest.getPageSize());
        return new RestResult(pagination, entries);
    }


    @GetMapping("/Obstacle/{id}")
    public Object getObstacleById(@PathVariable int id) {
        Obstacle obstacle = obstacleRepo.findById(id).orElse(null);

        if (obstacle == null) {
            String message = "Obstacle with id '" + id + "' not found!";
            return RestError.Type.NOT_FOUND.getErrorCode(message);
        }
        return obstacle;
    }

    @GetMapping("/Obstacles")
    public Collection<Obstacle> getObstacles() {
        return obstacleRepo.findAll();
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
