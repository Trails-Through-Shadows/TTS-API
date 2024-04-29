package cz.trailsthroughshadows.api.table.market.item;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.market.item.model.Item;
import cz.trailsthroughshadows.api.table.market.item.model.ItemDTO;
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
@RestController(value = "Item")
public class ItemController {

    private ValidationService validation;
    private ItemRepo itemRepo;

    @GetMapping("/items")
    @Cacheable(value = "item")
    public ResponseEntity<RestPaginatedResult<Item>> findAllEntities(
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

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage.stream().map(Item::fromDTO).toList()), HttpStatus.OK);
    }

    @GetMapping("/items/{id}")
    @Cacheable(value = "item", key = "#id")
    public ResponseEntity<Item> findById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        ItemDTO entity = itemRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Item with id '{}' not found! " + id));

        if (lazy && !include.isEmpty()) {
            Initialization.hibernateInitializeAll(entity, include);
        } else if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        }

        return new ResponseEntity<>(Item.fromDTO(entity), HttpStatus.OK);
    }

    @PostMapping("/items")
    @Cacheable(value = "item")
    public ResponseEntity<MessageResponse> createEntity(@RequestBody List<ItemDTO> enTITIES) {
        enTITIES.forEach(validation::validate);
        List<ItemDTO> saved = itemRepo.saveAll(enTITIES);
        String ids = saved.stream().map(ItemDTO::getId).map(String::valueOf).toList().toString();
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Actions with ids '%d' created!", ids),
                HttpStatus.OK);
    }

    @PutMapping("/items/{id}")
    @Cacheable(value = "item", key = "#id")
    public ResponseEntity<MessageResponse> update(@PathVariable int id, @RequestBody ItemDTO item) {
        validation.validate(item);

        ItemDTO entityToUPdate = itemRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Item with id '{}' not found!", id));

        entityToUPdate.setAction(item.getAction());
        entityToUPdate.setTitle(item.getTitle());
        entityToUPdate.setDescription(item.getDescription());
        entityToUPdate.setTag(item.getTag());
        entityToUPdate.setRequirements(item.getRequirements());
        entityToUPdate.setEffects(item.getEffects());
        entityToUPdate.setType(item.getType());

        itemRepo.save(entityToUPdate);

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Item with id '{}' updated!", id), HttpStatus.OK);
    }

    @DeleteMapping("/items/{id}")
    @Cacheable(value = "item", key = "#id")
    public ResponseEntity<MessageResponse> delete(@PathVariable int id) {
        ItemDTO entityToDelete = itemRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Item with id '{}' not found!", id));

        itemRepo.delete(entityToDelete);

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Item with id '{}' deleted!", id), HttpStatus.OK);
    }

    @Autowired
    public void setRepository(ItemRepo repository) {
        this.itemRepo = repository;
    }

    @Autowired
    public void setValidation(ValidationService validation) {
        this.validation = validation;
    }
}
