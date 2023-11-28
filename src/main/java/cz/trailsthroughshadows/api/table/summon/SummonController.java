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
    public SummonModel findById(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid summon Id:" + id));
    }

    @GetMapping("/")
    public Collection<SummonModel> findClass() {
        return repository.getAll();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SummonModel updateClass(
            @PathVariable("id") final String id, @RequestBody final SummonModel summon) {
        return summon;
    }
}
