package cz.trailsthroughshadows.api.table.action;

import cz.trailsthroughshadows.api.table.action.movement.Movement;
import cz.trailsthroughshadows.api.table.action.movement.MovementRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;


@RestController
@RequestMapping("/api/action")
public class ActionController {

    @Autowired
    private ActionRepo repository;
    @Autowired
    private MovementRepo movementRepo;

    @GetMapping("/{id}")
    public Action findById(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid action Id:" + id));
    }

    @GetMapping("movement/{id}")
    public Movement findMovementById(@PathVariable int id) {
        return movementRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid action Id:" + id));
    }

    @GetMapping("/all")
    public Collection<Action> findAll() {
        return repository.findAll();
    }

}
