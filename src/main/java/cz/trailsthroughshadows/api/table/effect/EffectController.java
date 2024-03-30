package cz.trailsthroughshadows.api.table.effect;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.effect.model.Effect;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController(value = "Effect")
public class EffectController {

    private ValidationService validation;
    private EffectRepo effectRepo;

    @GetMapping("/effects")
    @Cacheable(value = "effect")
    public ResponseEntity<RestPaginatedResult<Effect>> getEnemies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "true") boolean lazy) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<EffectDTO> entries = effectRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<EffectDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit),
                entries.size(), page, limit);
        return new ResponseEntity<>(
                RestPaginatedResult.of(pagination, entriesPage.stream().map(Effect::fromDTO).toList()), HttpStatus.OK);
    }

    @GetMapping("/enum/effectType")
    public ResponseEntity<List<EffectDTO.EffectType>> getEffectType() {
        return new ResponseEntity<>(Arrays.asList(EffectDTO.EffectType.values()), HttpStatus.OK);
    }

    @GetMapping("/enum/effectTarget")
    public ResponseEntity<List<EffectDTO.EffectTarget>> getEffectTarget() {
        return new ResponseEntity<>(Arrays.asList(EffectDTO.EffectTarget.values()), HttpStatus.OK);
    }

    @GetMapping("/effects/{id}")
    // @Cacheable(value = "effect", key = "#id")
    public ResponseEntity<Effect> findById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy) {
        EffectDTO entity = effectRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Effect with id '%d' not found!", id));

        if (lazy && !include.isEmpty()) {
            Initialization.hibernateInitializeAll(entity, include);
        } else if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        }

        return new ResponseEntity<>(Effect.fromDTO(entity), HttpStatus.OK);
    }

    @PutMapping("/effects/{id}")
    public ResponseEntity<MessageResponse> updateEffect(@PathVariable int id, @RequestBody EffectDTO effect) {
        validation.validate(effect);
        EffectDTO existing = effectRepo.findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Effect with id '%d' not found!", id));

        existing.setDescription(effect.getDescription());
        existing.setDuration(effect.getDuration());
        existing.setType(effect.getType());
        existing.setTarget(effect.getTarget());
        existing.setDuration(effect.getDuration());
        existing.setDescription(effect.getDescription());

        effectRepo.save(effect);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Effect updated successfully!"), HttpStatus.OK);

    }

    @PostMapping("/effects")
    public ResponseEntity<MessageResponse> createEffect(@RequestBody List<EffectDTO> effect) {
        effect.forEach(validation::validate);
        effect.forEach(e -> e.setId(null));
        effectRepo.saveAll(effect);

        String ids = effect.stream().map(e -> String.valueOf(e.getId())).toList().toString();
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Effects with ids '%s'", ids), HttpStatus.OK);
    }

    @DeleteMapping("/effects/{id}")
    public ResponseEntity<MessageResponse> deleteEffect(@PathVariable int id) {
        EffectDTO effect = effectRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Effect with id '%d' not found!", id));

        effectRepo.delete(effect);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Effect with id '%d' deleted!", id),
                HttpStatus.OK);
    }

    @Autowired
    public void setRepository(EffectRepo repository) {
        this.effectRepo = repository;
    }

    @Autowired
    public void setValidation(ValidationService validation) {
        this.validation = validation;
    }
}
