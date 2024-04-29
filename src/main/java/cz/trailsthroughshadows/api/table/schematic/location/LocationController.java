package cz.trailsthroughshadows.api.table.schematic.location;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.campaign.CampaignRepo;
import cz.trailsthroughshadows.api.table.schematic.hex.model.Hex;
import cz.trailsthroughshadows.api.table.schematic.location.model.Location;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import cz.trailsthroughshadows.api.table.schematic.part.model.PartDTO;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController(value = "Location")
public class LocationController {

    private ValidationService validation;
    private LocationRepo locationRepo;
    private CampaignRepo campaignRepo;

    @GetMapping("/locations")
    public ResponseEntity<RestPaginatedResult<Location>> getLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        // TODO: Re-Implement filtering, sorting and pagination
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<LocationDTO> entries = locationRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<LocationDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
//            if (include.contains("stories")) {
//                List<Location> loc = entriesPage.stream().map(Location::fromDTO).toList();
//                //loc.forEach(l -> l.findStories(campaignRepo));
//                return new ResponseEntity<>(RestPaginatedResult.of(pagination, loc), HttpStatus.OK);
//            }
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
//            List<Location> loc = entriesPage.stream().map(Location::fromDTO).toList();
//            //loc.forEach(l -> l.findStories(campaignRepo));
//            return new ResponseEntity<>(RestPaginatedResult.of(pagination, loc), HttpStatus.OK);
        }

        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage.stream().map(Location::fromDTO).toList()), HttpStatus.OK);
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<Location> getLocationById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        LocationDTO entity = locationRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Location with id '{}' not found! " + id));

        if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        } else {
            Initialization.hibernateInitializeAll(entity, include);
        }


        return new ResponseEntity<>(Location.fromDTO(entity), HttpStatus.OK);
    }

    @GetMapping("/locations/{idLocation}/parts/{idPart}")
    public ResponseEntity<Part> getPartByLocationId(@PathVariable int idLocation, @PathVariable int idPart) {

        LocationDTO locationDTO = locationRepo
                .findById(idLocation)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Location with id '{}' not found!", idLocation));
        Location location = Location.fromDTO(locationDTO);

        PartDTO part = location.getMappedParts().stream()
                .filter(p -> p.getId() == idPart)
                .findFirst()
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Part with id '{}' not found!", idPart));
        // TODO zoze add doors here

        Part retPart = Part.fromDTO(part, location.getObstacles(), location.getDoors());
        List<Hex> startingHexes = location.getStartingHexes().stream()
                .filter(hex -> hex.getKey().getIdPart() == idPart)
                .toList();
        retPart.setStartingHexes(startingHexes);

        return new ResponseEntity<>(retPart, HttpStatus.OK);
    }

    @PostMapping("/locations")
    @CacheEvict(value = "location", allEntries = true)
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<MessageResponse> createLocation(@RequestBody List<LocationDTO> locations) {
        log.debug("Creating locations: " + locations);

        // Validate all locations
        locations.forEach(validation::validate);

        locationRepo.saveAll(locations);

        String ids = locations.stream()
                .map(LocationDTO::getId)
                .map(String::valueOf)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Locations created: " + ids), HttpStatus.OK);
    }

    @PutMapping("/locations/{id}")
    @CacheEvict(value = "location", allEntries = true)
    public ResponseEntity<MessageResponse> updateLocationById(@PathVariable int id, @RequestBody LocationDTO location) {
        LocationDTO locationToUpdate = locationRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Location with id '{}' not found!", id));

        validation.validate(location);

        locationToUpdate.setTag(location.getTag());
        locationToUpdate.setTitle(location.getTitle());
        locationToUpdate.setDescription(location.getDescription());
        locationToUpdate.setEnemies(location.getEnemies());
        locationToUpdate.setObstacles(location.getObstacles());
        locationToUpdate.setDoors(location.getDoors());
        locationToUpdate.setParts(location.getParts());
        locationToUpdate.setStartHexes(location.getStartHexes());
        locationToUpdate.setType(location.getType());

        locationRepo.save(locationToUpdate);

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Location updated!"), HttpStatus.OK);
    }

    @DeleteMapping("/locations/{id}")
    @CacheEvict(value = "location", allEntries = true)
    public ResponseEntity<MessageResponse> deleteLocationById(@PathVariable int id) {
        LocationDTO location = locationRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Location with id '{}' not found!", id));

        locationRepo.delete(location);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Location deleted!"), HttpStatus.OK);
    }

    /**
     * ===============================================
     */

    @Autowired
    private void setLocationRepo(LocationRepo locationRepo) {
        this.locationRepo = locationRepo;
    }

    @Autowired
    public void setValidation(ValidationService validation) {
        this.validation = validation;
    }

    @Autowired
    public void setCampaignRepo(CampaignRepo campaignRepo) {
        this.campaignRepo = campaignRepo;
    }
}
