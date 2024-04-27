package cz.trailsthroughshadows.api.table.schematic.location;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.table.campaign.CampaignRepo;
import cz.trailsthroughshadows.api.table.schematic.hex.model.Hex;
import cz.trailsthroughshadows.api.table.schematic.location.model.Location;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import cz.trailsthroughshadows.api.table.schematic.part.model.PartDTO;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Location with id '%d' not found! " + id));

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
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Location with id '%d' not found!", idLocation));
        Location location = Location.fromDTO(locationDTO);

        PartDTO part = location.getMappedParts().stream()
                .filter(p -> p.getId() == idPart)
                .findFirst()
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Part with id '%d' not found!", idPart));
        // TODO zoze add doors here

        Part retPart = Part.fromDTO(part, location.getObstacles(), location.getDoors());
        List<Hex> startingHexes = location.getStartingHexes().stream()
                .filter(hex -> hex.getKey().getIdPart() == idPart)
                .toList();
        retPart.setStartingHexes(startingHexes);

        return new ResponseEntity<>(retPart, HttpStatus.OK);
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
