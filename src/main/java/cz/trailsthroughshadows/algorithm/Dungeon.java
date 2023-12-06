package cz.trailsthroughshadows.algorithm;

import cz.trailsthroughshadows.api.table.playerdata.Character;
import cz.trailsthroughshadows.api.table.enemy.Enemy;
import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import cz.trailsthroughshadows.api.table.schematic.location.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class Dungeon {

    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Character> characters = new ArrayList<>();
    private final Location location;

    public void moveCharacter(Character character, Hex hex) {
        character.setHex(hex);
    }

    public void moveEnemy(Enemy enemy, Hex hex) {
        enemy.setHex(hex);
    }

    public List<Hex> getNeighbors(Hex hex) {
        ArrayList<Hex> neighbors = new ArrayList<>();

        // TODO: implement this method

        return neighbors;
    }
}
