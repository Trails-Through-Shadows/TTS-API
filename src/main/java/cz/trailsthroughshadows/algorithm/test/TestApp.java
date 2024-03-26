package cz.trailsthroughshadows.algorithm.test;

import cz.trailsthroughshadows.api.table.action.features.summon.SummonRepo;
import cz.trailsthroughshadows.api.table.enemy.EnemyRepo;
import cz.trailsthroughshadows.api.table.playerdata.character.CharacterRepo;
import cz.trailsthroughshadows.api.table.schematic.location.LocationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestApp {

    @Autowired
    LocationRepo locationRepo;

    @Autowired
    CharacterRepo characterRepo;

    @Autowired
    EnemyRepo enemyRepo;

    @Autowired
    SummonRepo summonRepo;

    @GetMapping
    public Object test() {
//        Location loc = locationRepo.findById(6).get();
//        Dungeon dungeon = new Dungeon(loc);
//
//        Character character = characterRepo.findById(1).get();
//        character.setStartingHex(loc.getLocationParts().get(0).getHexes().get(32 - 1));
//        dungeon.getCharacters().add(character);
//
//        Enemy enemy = enemyRepo.findById(1).get();
//
//        Enemy e1 = enemy.clone();
//        e1.setStartingHex(loc.getLocationParts().get(0).getHexes().get(21 - 1));
//        dungeon.getEnemies().add(e1);
//
//        Enemy e2 = enemy.clone();
//        e2.setStartingHex(loc.getLocationParts().get(0).getHexes().get(61 - 1));
//        dungeon.getEnemies().add(e2);
//
//        var x = dungeon.calculateTarget(character, 3, Effect.EffectTarget.ONE);
//
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        return gson.toJson(x);
        return null;
    }
}
