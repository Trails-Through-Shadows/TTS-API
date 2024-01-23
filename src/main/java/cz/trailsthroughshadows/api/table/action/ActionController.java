package cz.trailsthroughshadows.api.table.action;

import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.table.action.movement.MovementRepo;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.Collection;


@RestController
@RequestMapping("/actions")
public class ActionController {

    @Autowired
    private ActionRepo actionRepo;
    @Autowired
    private MovementRepo movementRepo;

    @GetMapping("/{id}")
    public Action findById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        Action action = actionRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '%d' not found! " + id));

        if (!lazy) {
            for (Field F : action.getClass().getFields()) {
                try {
                    Hibernate.initialize(F.get(action));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return action;

    }

    @GetMapping("")
    public Collection<Action> findAllActions(
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        Collection<Action> actions = actionRepo.findAll();
        if (!lazy) {
            actions.forEach(action -> {
                Hibernate.initialize(action.getMovement());
                Hibernate.initialize(action.getSkill());
                Hibernate.initialize(action.getAttack());
                Hibernate.initialize(action.getRestoreCards());
                Hibernate.initialize(action.getSummonActions());
            });
        }
        return actions;
    }

}
