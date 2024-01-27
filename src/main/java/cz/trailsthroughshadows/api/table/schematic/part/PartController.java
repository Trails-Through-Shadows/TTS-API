package cz.trailsthroughshadows.api.table.schematic.part;

import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.Pagination;
import cz.trailsthroughshadows.api.rest.model.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.RestResponse;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.rest.model.error.type.MessageError;
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
@Cacheable(value = "part")
@RestController(value = "Part")
public class PartController {
    private PartRepo partRepo;

    @GetMapping("/parts")
    public ResponseEntity<RestPaginatedResult<Part>> getParts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter, // TODO: Re-Implement filtering
            @RequestParam(defaultValue = "") String sort // TODO: Re-Implement sorting
    ) {
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
        return RestResponse.of( HttpStatus.OK, "Part deleted!");
    }

    @PutMapping("/parts/{id}")
    @CacheEvict(value = "part", allEntries = true)
    public ResponseEntity<RestResponse> updatePartById(@PathVariable int id, @RequestBody Part part) {
        Part partToUpdate = partRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Part with id '%d' not found!", id));

        // Every save is adding some new hexes..
        partRepo.save(part);
        return RestResponse.of(HttpStatus.OK,"Part updated!");
    }

    @PostMapping("/parts")
    @CacheEvict(value = "part", allEntries = true)
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
        return RestResponse.of(HttpStatus.OK,"Parts created!");
    }

    /**
     * ===============================================
     */

    @Autowired
    public void setPartRepo(PartRepo partRepo) {
        this.partRepo = partRepo;
    }
}