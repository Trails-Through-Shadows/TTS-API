package cz.trailsthroughshadows.api.table.achievement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/achievements")
public class AchievementController {

    @Autowired
    private AchievementRepo achievementRepo;

    @GetMapping("/{id}")
    public Achievement findById(@PathVariable int id) {
        return achievementRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid achievement Id:" + id));
    }

    @GetMapping("")
    public Collection<Achievement> findClass() {
        return achievementRepo.getAll();
    }
}