package cz.trailsthroughshadows.api.table.effect;

import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.table.effect.model.Effect;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class EffectController {

    private EffectRepo effectRepo;

    @GetMapping("/effects")
    @Cacheable(value = "effect")
    public ResponseEntity<RestPaginatedResult<Effect>> getEnemies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort
    ) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<Effect> entries = effectRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .map(Effect::fromDTO)
                .toList();

        List<Effect> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage), HttpStatus.OK);
    }

    @GetMapping("/effects/{id}")
    @Cacheable(value = "effect", key = "#id")
    public Effect findById(
            @PathVariable int id
    ) {
        return effectRepo
                .findById(id)
                .map(Effect::fromDTO)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Effect with id '%d' not found! " + id));
    }

    @Autowired
    public void setRepository(EffectRepo repository) {
        this.effectRepo = repository;
    }
}
