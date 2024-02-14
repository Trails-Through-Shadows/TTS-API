package cz.trailsthroughshadows.api.table.action;

import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.table.action.features.movement.MovementRepo;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


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
        ActionDTO action = actionRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '%d' not found! " + id));

        if (lazy.isEmpty())
            Initialization.hibernateInitializeAll(action);
        else
            Initialization.hibernateInitializeAll(action, lazy);

        return action;

    }


    @GetMapping("")
    public Collection<ActionDTO> findAllActions(
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        Collection<ActionDTO> actions = actionRepo.findAll();
        if (!lazy) {
            Initialization.hibernateInitializeAll(actions);
        }
        return actions;
    }

}
