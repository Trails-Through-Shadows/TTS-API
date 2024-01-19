package cz.trailsthroughshadows.api.table.playerdata;

import cz.trailsthroughshadows.api.table.playerdata.adventure.Adventure;
import cz.trailsthroughshadows.api.table.playerdata.adventure.AdventureRepo;
import cz.trailsthroughshadows.api.table.playerdata.character.Character;
import cz.trailsthroughshadows.api.table.playerdata.character.CharacterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/playerdata/")
public class PlayerDataController {
    @Autowired
    private CharacterRepo repository;

    @GetMapping("/characters")
    public Collection<Character> getCharacters(@RequestParam(required = false) Integer idAdventure) {
        if (idAdventure != null) {
            return repository.getByAdventure(idAdventure);
        } else {
            return repository.getAll();
        }
    }

    @GetMapping("/characters/{id}")
    public Character findById(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid character Id:" + id));
    }

    // ADVENTURE SECTION

    @Autowired
    private AdventureRepo adventureRepo;

    @GetMapping("/adventures/{id}")
    public Adventure findAdventureById(@PathVariable int id) {
        return adventureRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Adventure Id: " + id));
    }

    @GetMapping("/adventures")
    public Collection<Adventure> findAdventures() {
        return adventureRepo.findAll();
    }
}
