package cz.trailsthroughshadows.api.table.character;

import cz.trailsthroughshadows.api.table.character.clazz.Clazz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/character")
public class CharacterController {

    @Autowired
    private CharacterRepo repository;

    @GetMapping
    public Collection<Character> getAll() {
        return repository.getAll();
    }

    @GetMapping("/{id}")
    public Character findById(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid character Id:" + id));
    }
}
