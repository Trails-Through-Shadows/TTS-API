package cz.trailsthroughshadows.api.table.character;

import cz.trailsthroughshadows.api.table.character.clazz.Clazz;
import cz.trailsthroughshadows.api.table.character.clazz.ClazzRepo;
import cz.trailsthroughshadows.api.table.character.race.Race;
import cz.trailsthroughshadows.api.table.character.race.RaceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/classrace")
public class ClassRaceController {

    @Autowired
    private ClazzRepo clazzRepo;

    @GetMapping("class/{id}")
    public Clazz findById(@PathVariable int id) {
        return clazzRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid class Id:" + id));
    }

    @GetMapping("class/")
    public Collection<Clazz> findClass() {
        return clazzRepo.getAll();
    }

    @PutMapping("class/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Clazz updateClass(
            @PathVariable("id") final String id, @RequestBody final Clazz clazz) {
        return clazz;
    }

    // RACE SECTION

    @Autowired
    private RaceRepo raceRepo;

    @GetMapping("race/{id}")
    public Race findRaceById(@PathVariable int id) {
        return raceRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Race Id: " + id));
    }

    @GetMapping("race/")
    public Collection<Race> findRaces() {
        return raceRepo.getAll();
    }
}
