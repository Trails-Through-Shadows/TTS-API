package cz.trailsthroughshadows.algorithm.encounter;

import cz.trailsthroughshadows.api.table.action.features.summon.model.Summon;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.schematic.location.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Encounter {

    private Integer idAdventure;
    private Location location;
    private List<Character> characters;
    private List<Enemy> enemies;
    private List<Summon> summons;


}
