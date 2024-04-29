package cz.trailsthroughshadows.api.table.schematic.part;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexDTO;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import cz.trailsthroughshadows.api.table.schematic.part.model.PartDTO;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController(value = "Part")
public class PartController {

    private ValidationService validation;
    private PartRepo partRepo;

    @GetMapping("/parts")
    @Cacheable(value = "parts")
    public ResponseEntity<RestPaginatedResult<Part>> getParts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        // TODO: Re-Implement filtering, sorting and pagination
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<PartDTO> entries = partRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<PartDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage.stream().map(Part::fromDTO).toList()), HttpStatus.OK);
    }

    @GetMapping("/parts/{id}")
    @Cacheable(value = "part", key = "#id")
    public ResponseEntity<Part> findById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        PartDTO entity = partRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Part with id '{}' not found!", id));

        if (lazy && !include.isEmpty()) {
            Initialization.hibernateInitializeAll(entity, include);
        } else if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        }

        return new ResponseEntity<>(Part.fromDTO(entity), HttpStatus.OK);
    }

    @DeleteMapping("/parts/{id}")
    @CacheEvict(value = "part", allEntries = true)
    public ResponseEntity<MessageResponse> deletePartById(@PathVariable int id) {
        PartDTO partDTO = partRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Part with id '{}' not found!", id));

        partRepo.delete(partDTO);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Part deleted!"), HttpStatus.OK);
    }

    @PutMapping("/parts/{id}")
    @CacheEvict(value = "part", allEntries = true)
    public ResponseEntity<MessageResponse> updatePartById(@PathVariable int id, @RequestBody PartDTO part) {
        PartDTO partToUpdate = partRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Part with id '{}' not found!", id));

        validation.validate(part);

        partToUpdate.setTag(part.getTag());
        partToUpdate.setHexes(part.getHexes());
        partToUpdate.setTitle(part.getTitle());

        partRepo.save(partToUpdate);

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Part updated!"), HttpStatus.OK);
    }

    @PostMapping("/parts")
    @CacheEvict(value = "part", allEntries = true)
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<MessageResponse> createParts(@RequestBody List<PartDTO> parts) {
        log.debug("Creating parts: " + parts);

        // Validate all parts
        parts.forEach(validation::validate);

        Map<String, List<HexDTO>> partHexes = new HashMap<>();
        parts.forEach(part -> {
            partHexes.put(part.getTag(), new ArrayList<>(part.getHexes()));
            part.setId(null); // Removing id to always create new part
            part.setHexes(null);
        });

        // Update Parts
        parts = partRepo.saveAll(parts);

        parts.forEach(part -> {
            List<HexDTO> hexes = partHexes.get(part.getTag());

            for (int i = 0; i < hexes.size(); i++) {
                hexes.get(i).setKey(new HexDTO.HexId(part.getId(), i));
            }

            part.setHexes(hexes);
        });

        parts = partRepo.saveAll(parts);

        String ids = parts.stream().map((part) -> String.valueOf(part.getId())).collect(Collectors.joining(", "));
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Parts created: " + ids), HttpStatus.OK);
    }

    /**
     * ===============================================
     */

    @Autowired
    private void setPartRepo(PartRepo partRepo) {
        this.partRepo = partRepo;
    }

    @Autowired
    public void setValidation(ValidationService validation) {
        this.validation = validation;
    }
}
