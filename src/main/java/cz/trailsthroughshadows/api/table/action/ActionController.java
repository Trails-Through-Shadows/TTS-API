package cz.trailsthroughshadows.api.table.action;

import cz.trailsthroughshadows.api.table.action.movement.MovementRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;


@RestController
@RequestMapping("/actions")
public class ActionController {

    @Autowired
    private ActionRepo actionRepo;
    @Autowired
    private MovementRepo movementRepo;

    @GetMapping("/{id}")
    public Action findById(@PathVariable int id) {
        return actionRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid action Id:" + id));
    }

    @GetMapping("")
    public Collection<Action> findAllActions() {
        return actionRepo.findAll();
    }

}
