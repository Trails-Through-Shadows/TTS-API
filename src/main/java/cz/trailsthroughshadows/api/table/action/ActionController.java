package cz.trailsthroughshadows.api.table.action;

import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.Pagination;
import cz.trailsthroughshadows.api.rest.model.RestPaginatedResult;
import cz.trailsthroughshadows.api.table.action.features.movement.MovementRepo;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("/actions")
@Slf4j
public class ActionController {

    @Autowired
    private ActionRepo actionRepo;

    @Autowired
    private MovementRepo movementRepo;

    @GetMapping("/{id}")
    public ActionDTO findById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> lazy
    ) {
        ActionDTO entity = actionRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '%d' not found! " + id));

        if (lazy.isEmpty())
            Initialization.hibernateInitializeAll(entity);
        else
            Initialization.hibernateInitializeAll(entity, lazy);

        return entity;

    }


    @GetMapping("")
    public ResponseEntity<RestPaginatedResult<ActionDTO>> findAllEntities(
            @RequestParam(required = false, defaultValue = "") List<String> lazy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort
    ) {
        Collection<ActionDTO> entities = actionRepo.findAll();

        if (!lazy.isEmpty())
            entities.forEach(e -> Initialization.hibernateInitializeAll(e, lazy));

        Pagination pagination = new Pagination(entities.size(), false, entities.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entities), HttpStatus.OK);
    }

}
