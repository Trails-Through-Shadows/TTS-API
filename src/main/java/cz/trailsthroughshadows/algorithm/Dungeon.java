package cz.trailsthroughshadows.algorithm;

import cz.trailsthroughshadows.algorithm.entity.Entity;
import cz.trailsthroughshadows.algorithm.location.LocationImpl;
import cz.trailsthroughshadows.api.table.action.summon.Summon;
import cz.trailsthroughshadows.api.table.playerdata.character.Character;
import cz.trailsthroughshadows.api.table.effect.Effect;
import cz.trailsthroughshadows.api.table.enemy.Enemy;
import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
@Log4j2
public class Dungeon {

    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Character> characters = new ArrayList<>();
    private final ArrayList<Summon> summons = new ArrayList<>();
    private final LocationImpl location;

    public void moveCharacter(Character character, Hex hex) {
        character.setHex(hex);
    }

    public void moveEnemy(Enemy enemy, Hex hex) {
        enemy.setHex(hex);
    }

    public List<Entity> calculateTarget(Entity entity, int range, Effect.EffectTarget target) {
        List<Entity> targets = new ArrayList<>();
        Hex hex = entity.getHex();

        log.info("Calculating target for " + entity.toString() + " at " + hex + " with range " + range + " and target " + target);

        switch (target) {
            case SELF:
                targets.add(entity);
                break;
            case ALL_ALLIES:
                targets.addAll(characters);
                targets.addAll(summons);
                break;
            case ALL_ENEMIES:
                targets.addAll(enemies);
                break;
            case ALL:
                for (Entity ent : Stream.of(characters, summons, enemies).flatMap(List::stream).toList()) {
                    if (location.getDistance(hex, ent.getHex()) <= range) {
                        targets.add(ent);
                    }
                }
                break;
            case ONE:
                here:
                for (int i = 0; i <= range; i++) {
                    for (Hex neighbor : location.getNeighbors(hex, i)) {
                        for (Entity ent : Stream.of(characters, summons, enemies).flatMap(List::stream).toList()) {
                            if (neighbor == ent.getHex() && ent != entity) {
                                targets.add(ent);
                            }
                        }
                        if (!targets.isEmpty()) break here;
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + target);
        }

        return targets;
    }
}
