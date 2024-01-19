package cz.trailsthroughshadows.api.table.action.summon;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/summons")
@Slf4j
public class SummonController {

    @Autowired
    private SummonRepo repository;

    @GetMapping("/{id}")
    public Summon findById(@PathVariable int id) {
        return repository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Invalid Summon Id:" + id)
        );
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Summon updateClass(
            @PathVariable("id") final String id, @RequestBody final Summon summon) {
        return summon;
    }

    @GetMapping("")
    public Collection<Summon> findClass() {
        return repository.getAll();
    }
}
