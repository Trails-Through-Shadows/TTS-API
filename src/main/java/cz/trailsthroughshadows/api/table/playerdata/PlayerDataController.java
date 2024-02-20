package cz.trailsthroughshadows.api.table.playerdata;

import cz.trailsthroughshadows.api.table.playerdata.adventure.AdventureRepo;
import cz.trailsthroughshadows.api.table.playerdata.adventure.model.AdventureDTO;
import cz.trailsthroughshadows.api.table.playerdata.character.CharacterRepo;
import cz.trailsthroughshadows.api.table.playerdata.character.model.CharacterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/playerdata/")
public class PlayerDataController {
    @Autowired
    private CharacterRepo repository;

    @Autowired
    private AdventureRepo adventureRepo;

    @GetMapping("/characters")
    public Collection<CharacterDTO> getCharacters(@RequestParam(required = false) Integer idAdventure) {
        if (idAdventure != null) {
            return repository.getByAdventure(idAdventure);
        } else {
            return repository.getAll();
        }
    }

    // ADVENTURE SECTION

    @GetMapping("/characters/{id}")
    public CharacterDTO findById(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid character Id:" + id));
    }
}
