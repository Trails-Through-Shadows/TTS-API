package cz.trailsthroughshadows.api.table.achievement;

import cz.trailsthroughshadows.api.table.action.summon.Summon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/achievement")
public class AchievementController {

    @Autowired
    private AchievementRepo repository;

    @GetMapping("/{id}")
    public Achievement findById(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid achievement Id:" + id));
    }

    @GetMapping("/")
    public Collection<Achievement> findClass() {
        return repository.getAll();
    }
}