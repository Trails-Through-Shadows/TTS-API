package cz.trailsthroughshadows.api.table.achievement;

import cz.trailsthroughshadows.api.table.summon.Summon;
import cz.trailsthroughshadows.api.table.summon.SummonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Achievement")
public class AchievementController {

    @Autowired
    private AchievementRepo repository;

    @GetMapping("/{id}")
    public Summon findById(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid summon Id:" + id));
    }
}