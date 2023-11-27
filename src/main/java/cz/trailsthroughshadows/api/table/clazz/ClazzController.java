package cz.trailsthroughshadows.api.table.clazz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/Class")
public class ClazzController {

    @Autowired
    private ClazzRepo repository;

    @GetMapping("/{id}")
    public ClazzModel findById(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid class Id:" + id));
    }

    @GetMapping("/")
    public Collection<ClazzModel> findClass() {
        return repository.getAll();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClazzModel updateClass(
            @PathVariable("id") final String id, @RequestBody final ClazzModel clazz) {
        return clazz;
    }
}
