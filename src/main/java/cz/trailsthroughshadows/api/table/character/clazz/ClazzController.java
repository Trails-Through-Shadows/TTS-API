package cz.trailsthroughshadows.api.table.character.clazz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/class")
public class ClazzController {

    @Autowired
    private ClazzRepo repository;

    @GetMapping("/{id}")
    public Clazz findById(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid class Id:" + id));
    }

    @GetMapping("/")
    public Collection<Clazz> findClass() {
        return repository.getAll();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Clazz updateClass(
            @PathVariable("id") final String id, @RequestBody final Clazz clazz) {
        return clazz;
    }
}
