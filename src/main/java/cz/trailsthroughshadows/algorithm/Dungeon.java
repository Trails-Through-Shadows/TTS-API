package cz.trailsthroughshadows.algorithm;

import cz.trailsthroughshadows.algorithm.entity.Entity;
import cz.trailsthroughshadows.algorithm.location.LocationImpl;
import cz.trailsthroughshadows.api.table.action.summon.Summon;
import cz.trailsthroughshadows.api.table.effect.Effect;
import cz.trailsthroughshadows.api.table.playerdata.Character;
import cz.trailsthroughshadows.api.table.enemy.Enemy;
import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
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

    public List<Entity> calculateTarget (Entity entity, int range, Effect.EffectTarget target) {
        List<Entity> targets = new ArrayList<>();
        Hex hex = entity.getHex();

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
                targets.addAll(characters);
                targets.addAll(summons);
                targets.addAll(enemies);
                break;
            case ONE:
                for (Hex neighbor : location.getNeighbors(hex, range)) {
                    targets.addAll(characters.stream().filter(character -> character.getHex() == neighbor).toList());
                    targets.addAll(summons.stream().filter(summon -> summon.getHex() == neighbor).toList());
                    targets.addAll(enemies.stream().filter(enemy -> enemy.getHex() == neighbor).toList());
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + target);
        }



        return targets;
    }
}
