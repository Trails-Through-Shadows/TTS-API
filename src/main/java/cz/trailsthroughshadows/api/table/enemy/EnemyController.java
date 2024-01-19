package cz.trailsthroughshadows.api.table.enemy;

import cz.trailsthroughshadows.api.rest.model.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/enemies")
public class EnemyController {


    @Autowired
    private EnemyRepo repository;

    @GetMapping("/{id}")
    public Enemy findById(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid class Id:" + id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestResponse> updateClass(
            @PathVariable("id") final String id, @RequestBody final Enemy enemy) {
        repository.save(enemy);
        return new ResponseEntity<>(RestResponse.of("Enemy saved"), HttpStatus.OK);
    }


    @GetMapping("")
    public Collection<Enemy> findClass() {
        return repository.getAll();
    }
}