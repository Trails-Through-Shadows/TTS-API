package cz.trailsthroughshadows.api.table.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;


@RestController
@RequestMapping("/api/Action")
public class ActionController {

    @Autowired
    private ActionRepo repository;

    @GetMapping("/{id}")
    public Action findById(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid action Id:" + id));
    }

    @GetMapping("/")
    public Collection<Action> findAll() {
        return repository.getAll();
    }


}
