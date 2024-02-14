package cz.trailsthroughshadows.api.table.background;

import cz.trailsthroughshadows.api.table.background.clazz.ClazzRepo;
import cz.trailsthroughshadows.api.table.background.clazz.model.ClazzDTO;
import cz.trailsthroughshadows.api.table.background.race.RaceRepo;
import cz.trailsthroughshadows.api.table.background.race.model.RaceDTO;
import org.hibernate.Hibernate;
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
    public ClazzDTO findById(@PathVariable int id) {
        return clazzRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid class Id:" + id));
    }

    @GetMapping("classes")
    public Collection<ClazzDTO> findClass() {
        return clazzRepo.getAll();
    }

    @PutMapping("classes/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClazzDTO updateClass(
            @PathVariable("id") final String id, @RequestBody final ClazzDTO clazz) {
        return clazz;
    }

    // RACE SECTION

    @GetMapping("races/{id}")
    public RaceDTO findRaceById(
            @PathVariable int id,
            @RequestParam(defaultValue = "true") boolean lazyLoad
    ) {
        RaceDTO r = raceRepo.findById(id).orElseThrow();

        Hibernate.initialize(r);

        return r;
    }

    @GetMapping("races")
    public Collection<RaceDTO> findRaces() {
        return raceRepo.getAll();
    }
}
