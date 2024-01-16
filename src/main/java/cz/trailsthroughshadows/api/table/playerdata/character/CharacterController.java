package cz.trailsthroughshadows.api.table.playerdata.character;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
