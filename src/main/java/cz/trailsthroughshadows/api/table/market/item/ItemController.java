package cz.trailsthroughshadows.api.table.market.item;

import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.Pagination;
import cz.trailsthroughshadows.api.rest.model.RestPaginatedResult;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.market.item.model.Item;
import cz.trailsthroughshadows.api.table.market.item.model.ItemDTO;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cascade;
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
public class ItemController {

    private ItemRepo itemRepo;

    @GetMapping("/items")
    @Cacheable(value = "item")
    public ResponseEntity<RestPaginatedResult<ItemDTO>> findAllEntities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<ItemDTO> entries = itemRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<ItemDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        if (!lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entriesPage.size(), false, entriesPage.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage), HttpStatus.OK);
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<ItemDTO> findById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        ItemDTO entity = itemRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '%d' not found! " + id));

        if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        } else {
            Initialization.hibernateInitializeAll(entity, include);
        }

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @Autowired
    public void setRepository(ItemRepo repository) {
        this.itemRepo = repository;
    }
}
