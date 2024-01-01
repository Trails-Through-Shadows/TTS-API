package cz.trailsthroughshadows.api.table.schematic;

import cz.trailsthroughshadows.api.rest.Pagination;
import cz.trailsthroughshadows.api.rest.RestError;
import cz.trailsthroughshadows.api.rest.RestResult;
import cz.trailsthroughshadows.api.table.schematic.location.Location;
import cz.trailsthroughshadows.api.table.schematic.location.LocationRepo;
import cz.trailsthroughshadows.api.table.schematic.part.Part;
import cz.trailsthroughshadows.api.table.schematic.part.PartRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController(value = "Schematic")
public class SchematicController {
    private PartRepo partRepo;
    private LocationRepo locationRepo;

    @GetMapping("/parts")
    public RestResult getParts(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter
    ) {
        Collection<Part> entries = partRepo.findAll();
        List<Object> filteredEntries = entries.stream()
                .filter(entry -> {
                    if (filter.isEmpty()) {
                        return true;
                    } else {
                        return entry.getTag().toLowerCase().contains(filter.toLowerCase());
                    }
                })
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

        Pagination pagination = new Pagination(filteredEntries.size(), entries.size() > offset + limit, entries.size(), offset, limit);
        return new RestResult(pagination, filteredEntries);
    }

    @GetMapping("/part/{id}")
    public Object getPartById(@PathVariable int id) {
        Part part = partRepo.findById(id).orElse(null);

        if (part == null) {
            RestError.Code errorCode = RestError.Code.NOT_FOUND;
            String message = "Part with id " + id + " not found";
            return new RestError(errorCode, message);
        }

        return part;
    }

    @GetMapping("/locations")
    public RestResult getLocations(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter
    ) {
        Collection<Location> entries = locationRepo.findAll();
        List<Object> filteredEntries = entries.stream()
                .filter(entry -> {
                    if (filter.isEmpty()) {
                        return true;
                    } else {
                        return entry.getTag().toLowerCase().contains(filter.toLowerCase())
                                || entry.getTitle().toLowerCase().contains(filter.toLowerCase())
                                || entry.getDescription().toLowerCase().contains(filter.toLowerCase());
                    }
                })
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

        Pagination pagination = new Pagination(filteredEntries.size(), entries.size() > offset + limit, entries.size(), offset, limit);
        return new RestResult(pagination, filteredEntries);
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
}
