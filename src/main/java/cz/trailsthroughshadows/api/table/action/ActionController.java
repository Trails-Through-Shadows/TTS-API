package cz.trailsthroughshadows.api.table.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
