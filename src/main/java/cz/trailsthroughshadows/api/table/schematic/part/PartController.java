package cz.trailsthroughshadows.api.table.schematic.part;

import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.Pagination;
import cz.trailsthroughshadows.api.rest.model.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.RestResponse;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Cacheable(value = "part")
@RestController(value = "Part")
public class PartController {
    private PartRepo partRepo;

    @GetMapping("/parts")
    public ResponseEntity<RestPaginatedResult<Part>> getParts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort
    ) {
        // TODO: Re-Implement filtering, sorting and pagination
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<Part> entries = partRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<Part> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
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
    @CacheEvict(value = "part", allEntries = true)
    public ResponseEntity<RestResponse> deletePartById(@PathVariable int id) {
        Part part = partRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Part with id '%d' not found!", id));

        partRepo.delete(part);
        return RestResponse.of(HttpStatus.OK, "Part deleted!");
    }

    @PutMapping("/parts/{id}")
    @CacheEvict(value = "part", allEntries = true)
    public ResponseEntity<RestResponse> updatePartById(@PathVariable int id, @RequestBody Part part) {
        Part partToUpdate = partRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Part with id '%d' not found!", id));

        // TODO: Validation for new or updates parts

        partToUpdate.setTag(part.getTag());
        partToUpdate.setHexes(part.getHexes());

        partRepo.save(partToUpdate);

        return RestResponse.of(HttpStatus.OK, "Part updated!");
    }

    @PostMapping("/parts")
    @CacheEvict(value = "part", allEntries = true)
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<RestResponse> createParts(@RequestBody List<Part> parts) {

        log.debug("Creating parts: " + parts);

        // Ble
        // this needs rework
        try {
            for (Part part : parts) {
                List<Hex> hexes = new ArrayList<>(part.getHexes());
                part.getHexes().clear();
                part = partRepo.saveAndFlush(part);

                int partId = part.getId();
                log.trace("Part created: " + partId);
                hexes.forEach(hex -> {
                    hex.getKey().setIdPart(partId);
                    hex.getKey().setId(hexes.indexOf(hex) + 1);
                });

                part.setHexes(hexes);
                partRepo.save(part);
            }
        } catch (Exception e) {
            throw new RestException(RestError.of(HttpStatus.INTERNAL_SERVER_ERROR, String.format(e.getMessage())));
        }


        return RestResponse.of(HttpStatus.OK, "Parts created!");
    }

    /**
     * ===============================================
     */

    @Autowired
    private void setPartRepo(PartRepo partRepo) {
        this.partRepo = partRepo;
    }
}
