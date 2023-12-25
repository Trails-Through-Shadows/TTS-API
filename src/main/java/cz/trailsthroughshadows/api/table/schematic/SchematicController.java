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

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController(value = "Schematic")
public class SchematicController {

    private LocationRepo locationRepo;
    private PartRepo partRepo;

    @GetMapping("/locations")
    public RestResult getLocations(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "100") int limit
    ) {
        Collection<Location> locations = locationRepo.findAll();
        List<Object> filteredLocations = locations.stream().skip(offset).limit(limit).collect(Collectors.toList());

        Pagination pagination = new Pagination(locations.size(), locations.size() > offset + limit, filteredLocations.size(), offset, limit);
        return new RestResult(pagination, filteredLocations);
    }

    @GetMapping("/location/{id}")
    public Object getLocationById(
            @PathVariable int id
    ) {
        Location location = locationRepo.findById(id).orElse(null);

        if (location == null) {
            RestError.Code errorCode = RestError.Code.NOT_FOUND;
            String message = "Location with id " + id + " not found";
            return new RestError(errorCode, message, ZonedDateTime.now());
        }

        return location;
    }

    @GetMapping("/parts")
    public RestResult getParts(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "100") int limit
    ) {
        Collection<Part> parts = partRepo.findAll();
        List<Object> filteredParts = parts.stream().skip(offset).limit(limit).collect(Collectors.toList());

        Pagination pagination = new Pagination(parts.size(), parts.size() > offset + limit, filteredParts.size(), offset, limit);
        return new RestResult(pagination, filteredParts);
    }

    @GetMapping("/part/{id}")
    public Object getPartById(@PathVariable int id) {
        Part part = partRepo.findById(id).orElse(null);

        if (part == null) {
            RestError.Code errorCode = RestError.Code.NOT_FOUND;
            String message = "Part with id " + id + " not found";
            return new RestError(errorCode, message, ZonedDateTime.now());
        }

        return part;
    }

    /**
     * ===============================================
     */

    @Autowired
    public void setLocationRepo(LocationRepo locationRepo) {
        this.locationRepo = locationRepo;
    }

    @Autowired
    public void setPartRepo(PartRepo partRepo) {
        this.partRepo = partRepo;
    }
}
