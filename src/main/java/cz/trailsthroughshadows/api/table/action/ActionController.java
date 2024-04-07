package cz.trailsthroughshadows.api.table.action;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.action.model.Action;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController(value = "Action")
public class ActionController {

    private ValidationService validation;
    private ActionRepo actionRepo;

    @GetMapping("/actions")
    //@Cacheable(value = "action")
    public ResponseEntity<RestPaginatedResult<Action>> findAllEntities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<ActionDTO> entries = actionRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<ActionDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage.stream().map(Action::fromDTO).toList()), HttpStatus.OK);
    }

    @GetMapping("/actions/{id}")
    //@Cacheable(value = "action", key = "#id")
    public ResponseEntity<Action> findById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        ActionDTO entity = actionRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '%d' not found!", id));

        if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        } else {
            Initialization.hibernateInitializeAll(entity, include);
        }

        return new ResponseEntity<>(Action.fromDTO(entity), HttpStatus.OK);
    }

    @PutMapping("/actions/{id}")
    public ResponseEntity<MessageResponse> update(@PathVariable int id, @RequestBody ActionDTO action) {
        validation.validate(action);

        ActionDTO entityToUPdate = actionRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '%d' not found!", id));

        entityToUPdate.setTitle(action.getTitle());
        entityToUPdate.setDescription(action.getDescription());
        entityToUPdate.setDiscard(action.getDiscard());
        entityToUPdate.setLevelReq(action.getLevelReq());

        entityToUPdate.setAttack(action.getAttack());
        entityToUPdate.setMovement(action.getMovement());
        entityToUPdate.setSkill(action.getSkill());
        entityToUPdate.setRestoreCards(action.getRestoreCards());

        entityToUPdate.setSummonActions(action.getSummonActions());

        actionRepo.save(entityToUPdate);

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Action with id '%d' updated!", id),
                HttpStatus.OK);
    }

    @DeleteMapping("/actions/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable int id) {
        ActionDTO entity = actionRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '%d' not found!", id));

        actionRepo.delete(entity);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Action with id '%d' deleted!", id),
                HttpStatus.OK);
    }

    @PostMapping("/actions")
    public ResponseEntity<MessageResponse> create(@RequestBody List<ActionDTO> actions) {
        actions.forEach(validation::validate);
        actions.forEach(e -> e.setId(null));

        //remove relations and save them for later
        //todo summons

        actions = actionRepo.saveAll(actions);

        String ids = actions.stream().map(ActionDTO::getId).map(String::valueOf).toList().toString();
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Actions with ids '%s' created!", ids),
                HttpStatus.OK);
    }

    @Autowired
    public void setRepository(ActionRepo repository) {
        this.actionRepo = repository;
    }

    @Autowired
    public void setValidation(ValidationService validation) {
        this.validation = validation;
    }
}
