package cz.trailsthroughshadows.algorithm.encounter;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public class EncounterController {

    @PostMapping("/encounter/{idAdventure}")
    public boolean startEncounter(@RequestParam UUID token, @PathVariable Integer idAdventure, @RequestParam Integer idLocation, @RequestBody List<Integer> characters) {

        // get adventure
        //    check them
        // check session
        // get characters
        //    check them

        // get location
        //    check it
        //    check if the location is unlocked for the adventure

        // get enemies
        //    check them

        // get obstacles
        //    check them

        // create encounter

        return true;
    }
}
