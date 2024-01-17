package cz.trailsthroughshadows.api.table.enemy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/enemy")
public class EnemyController {


    @Autowired
    private EnemyRepo repository;

    @GetMapping("/{id}")
    public Enemy findById(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid class Id:" + id));
    }

    @GetMapping("/all")
    public Collection<Enemy> findClass() {
        return repository.getAll();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Enemy updateClass(
            @PathVariable("id") final String id, @RequestBody final Enemy enemy) {
        return enemy;
    }

}