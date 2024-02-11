package cz.trailsthroughshadows.api.table.schematic.location;

import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.Pagination;
import cz.trailsthroughshadows.api.rest.model.RestPaginatedResult;
import cz.trailsthroughshadows.api.table.schematic.location.model.Location;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
//@Cacheable(value = "location")
@RestController(value = "Location")
public class LocationController {

    private LocationRepo locationRepo;

    @GetMapping("/locations")
    public ResponseEntity<RestPaginatedResult<Location>> getLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort
    ) {
        // TODO: Re-Implement filtering, sorting and pagination
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<Location> entries = locationRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .map(Location::fromDTO)
                .toList();

        List<Location> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage), HttpStatus.OK);
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable int id) {
        LocationDTO locationDTO = locationRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Location with id '%d' not found!", id));

        return new ResponseEntity<>(Location.fromDTO(locationDTO), HttpStatus.OK);
    }

    @GetMapping("/locations/{locationId}/parts/{partId}")
    public ResponseEntity<Part> getPartByLocationId(@PathVariable int locationId, @PathVariable int partId) {

        LocationDTO locationDTO = locationRepo
                .findById(locationId)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Location with id '%d' not found!", locationId));
        Location location = Location.fromDTO(locationDTO);

        Part part = location.getParts().stream().filter(p -> p.getId() == partId).findFirst()
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Part with id '%d' not found!", partId));

        return new ResponseEntity<>(part, HttpStatus.OK);
    }

    /**
     * ===============================================
     */

    @Autowired
    private void setLocationRepo(LocationRepo locationRepo) {
        this.locationRepo = locationRepo;
    }
}
