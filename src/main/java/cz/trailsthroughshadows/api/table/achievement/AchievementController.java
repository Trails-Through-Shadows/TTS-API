package cz.trailsthroughshadows.api.table.achievement;

import cz.trailsthroughshadows.api.table.action.summon.Summon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/Achievement")
public class AchievementController {

    @Autowired
    private AchievementRepo repository;

    @GetMapping("/{id}")
    public Achievement findById(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid achievement Id:" + id));
    }

    @GetMapping("/")
    public Collection<Summon> findClass() {
        return repository.getAll();
    }
}