package cz.trailsthroughshadows.api.table.background;

import cz.trailsthroughshadows.api.table.background.clazz.Clazz;
import cz.trailsthroughshadows.api.table.background.clazz.ClazzRepo;
import cz.trailsthroughshadows.api.table.background.race.Race;
import cz.trailsthroughshadows.api.table.background.race.RaceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/background/")
public class BackgroundController {

    @Autowired
    private ClazzRepo clazzRepo;
    @Autowired
    private RaceRepo raceRepo;

    @GetMapping("classes/{id}")
    public Clazz findById(@PathVariable int id) {
        return clazzRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid class Id:" + id));
    }

    @GetMapping("classes/")
    public Collection<Clazz> findClass() {
        return clazzRepo.getAll();
    }

    @PutMapping("classes/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Clazz updateClass(
            @PathVariable("id") final String id, @RequestBody final Clazz clazz) {
        return clazz;
    }

    // RACE SECTION

    @GetMapping("races/{id}")
    public Race findRaceById(@PathVariable int id) {
        return raceRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Race Id: " + id));
    }

    @GetMapping("races/")
    public Collection<Race> findRaces() {
        return raceRepo.getAll();
    }
}
