package cz.trailsthroughshadows.algorithm.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cz.trailsthroughshadows.algorithm.Dungeon;
import cz.trailsthroughshadows.api.table.action.Action;
import cz.trailsthroughshadows.api.table.action.ActionRepo;
import cz.trailsthroughshadows.api.table.action.summon.SummonRepo;
import cz.trailsthroughshadows.api.table.character.Character;
import cz.trailsthroughshadows.api.table.character.CharacterRepo;
import cz.trailsthroughshadows.api.table.effect.Effect;
import cz.trailsthroughshadows.api.table.enemy.Enemy;
import cz.trailsthroughshadows.api.table.enemy.EnemyRepo;
import cz.trailsthroughshadows.api.table.schematic.location.Location;
import cz.trailsthroughshadows.api.table.schematic.location.LocationRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/test")
public class TestApp {

    @Autowired
    LocationRepo locationRepo;

    @Autowired
    CharacterRepo characterRepo;
    @Autowired
    EnemyRepo enemyRepo;
    @Autowired
    SummonRepo summonRepo;

    @Autowired
    ActionRepo actionRepo;

    @GetMapping
    public Object test() {
        log.info("Test");

        Location loc = locationRepo.findById(6).get();
        Dungeon dungeon = new Dungeon(loc);

        Character character = characterRepo.findById(1).get();
        character.setHex(loc.getLocationParts().get(0).getHexes().get(32 - 1));
        dungeon.getCharacters().add(character);

        Enemy enemy = enemyRepo.findById(1).get();

        Enemy e1 = enemy.clone();
        e1.setHex(loc.getLocationParts().get(0).getHexes().get(21 - 1));
        dungeon.getEnemies().add(e1);

        Enemy e2 = enemy.clone();
        e2.setHex(loc.getLocationParts().get(0).getHexes().get(41 - 1));
        dungeon.getEnemies().add(e2);

        Action action = actionRepo.findById(2).get();

        action.getAttack().setNumAttacks(3);
        action.getAttack().setDamage(6);
        action.getAttack().setTarget(Effect.EffectTarget.ALL);

        Effect effect = new Effect();
        effect.setTarget(Effect.EffectTarget.ONE);
        effect.setType(Effect.EffectType.BLEED);
        effect.setDuration(3);
        effect.setStrength(2);
//        action.getAttack().getEffects().add(effect);

        dungeon.evaluateAction(character, action);

        var x = dungeon.calculateTarget(character, Effect.EffectTarget.ONE, 3);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(x);
    }
}
