package cz.trailsthroughshadows.api.table.summon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/Summon")
public class SummonController {

    @Autowired
    private SummonRepo repository;

    @GetMapping("/{id}")
    public Summon findById(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid summon Id:" + id));
    }

    @GetMapping("/")
    public Collection<Summon> findClass() {
        return repository.getAll();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Summon updateClass(
            @PathVariable("id") final String id, @RequestBody final Summon summon) {
        return summon;
    }
}
