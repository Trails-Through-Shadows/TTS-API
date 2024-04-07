package cz.trailsthroughshadows.api.table.background.clazz;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.background.clazz.model.Clazz;
import cz.trailsthroughshadows.api.table.background.clazz.model.ClazzDTO;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController(value = "Class")
public class ClazzController {

    private ValidationService validation;
    private ClazzRepo clazzRepo;

    @GetMapping("/classes")
    @Cacheable(value = "class")
    public ResponseEntity<RestPaginatedResult<Clazz>> getEnemies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<ClazzDTO> entries = clazzRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<ClazzDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage.stream().map(Clazz::fromDTO).toList()), HttpStatus.OK);
    }

    @GetMapping("/classes/{id}")
    @Cacheable(value = "enemy", key = "#id")
    public ResponseEntity<Clazz> findById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        ClazzDTO entity = clazzRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Class with id '%d' not found! ", id));

        if (lazy && !include.isEmpty()) {
            Initialization.hibernateInitializeAll(entity, include);
        } else if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        }

        return new ResponseEntity<>(Clazz.fromDTO(entity), HttpStatus.OK);
    }

    @PutMapping("classes/{id}")
    public ResponseEntity<MessageResponse> updateEntity(
            @PathVariable int id,
            @RequestBody ClazzDTO entity
    ) {
        validation.validate(entity);

        ClazzDTO clazz = clazzRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Class with id '%d' not found! ", id));

        clazz.setTitle(entity.getTitle());
        clazz.setTag(entity.getTag());
        clazz.setDescription(entity.getDescription());
        clazz.setBaseHealth(entity.getBaseHealth());
        clazz.setBaseDefence(entity.getBaseDefence());
        clazz.setBaseInitiative(entity.getBaseInitiative());

        clazz.setActions(entity.getActions());
        clazz.setEffects(entity.getEffects());

        ClazzDTO updated = clazzRepo.save(clazz);

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK ,"Class with id '%d' updated!", updated.getId()), HttpStatus.OK);
    }

    @Autowired
    public void setRepository(ClazzRepo repository) {
        this.clazzRepo = repository;
    }

    @Autowired
    public void setValidation(ValidationService validation) {
        this.validation = validation;
    }
}
