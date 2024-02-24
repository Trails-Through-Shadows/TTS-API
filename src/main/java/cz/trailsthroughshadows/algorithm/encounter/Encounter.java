package cz.trailsthroughshadows.algorithm.encounter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.encounter.model.EncounterEntity;
import cz.trailsthroughshadows.algorithm.encounter.model.Initiative;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.table.action.features.summon.SummonRepo;
import cz.trailsthroughshadows.api.table.action.features.summon.model.Summon;
import cz.trailsthroughshadows.api.table.enemy.EnemyRepo;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.playerdata.adventure.model.AdventureDTO;
import cz.trailsthroughshadows.api.table.playerdata.character.CharacterRepo;
import cz.trailsthroughshadows.api.table.playerdata.character.model.Character;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@JsonSerialize(using = EncounterSerializer.class)
public class Encounter {

    private CharacterRepo characterRepo;
    private EnemyRepo enemyRepo;
    private SummonRepo summonRepo;

    private Integer id;
    private Integer idLicense;
    private AdventureDTO adventure;
    private LocationDTO location;
    private EncounterState state = EncounterState.ONGOING;
    private List<EncounterEntity<?>> entities = new ArrayList<>();

    public Encounter(Integer id, Integer idLicense, AdventureDTO adventure, LocationDTO location, List<Character> characters, List<Enemy> enemies, List<Summon> summons, List<Obstacle> obstacles) {
        this.id = id;
        this.idLicense = idLicense;
        this.adventure = adventure;
        this.location = location;

        entities.addAll(characters.stream()
                .map(c -> new EncounterEntity<>(c.getId(), c.getInitiative(), EncounterEntity.EntityType.CHARACTER, c))
                .toList());

        entities.addAll(enemies.stream()
                .map(e -> new EncounterEntity<>(entities.size(), e.getId(), e.getInitiative(), EncounterEntity.EntityType.ENEMY, e))
                .toList());

        // todo add custom id handling
    }

    public List<Initiative> getInitiative() {
        List<Initiative> initiatives = new ArrayList<>();

        initiatives.addAll(getCharacters().stream()
                .map(c -> new Initiative(c.getId(), c.getInitiative(), EncounterEntity.EntityType.CHARACTER))
                .toList());

        initiatives.addAll(getEnemyGroups().stream()
                .map(e -> new Initiative(e.getId(), e.getInitiative(), EncounterEntity.EntityType.ENEMY))
                .toList());

        initiatives.sort((i1, i2) -> i2.getInitiative().compareTo(i1.getInitiative()));

        return initiatives;
    }

    public void rollInitiative(List<Initiative> initiatives) {
        for (Initiative initiative : initiatives) {
            Character character = getCharacters().stream()
                    .filter(c -> c.getId().equals(initiative.getId()))
                    .findFirst()
                    .orElseThrow(() -> RestException.of(HttpStatus.UNAUTHORIZED, "Character not found"));
        }
    }

    private <T> List<T> getEntities(Class<?> c) {
        return entities.stream()
                .filter(e -> e.getEntity().getClass().equals(c))
                .map(e -> (T) e.getEntity())
                .collect(Collectors.toList());
    }

    public List<Enemy> getEnemies() {
        return getEntities(Enemy.class);
    }

    public List<Character> getCharacters() {
        return getEntities(Character.class);
    }

    public List<Summon> getSummons() {
        return getEntities(Summon.class);
    }

    public List<Obstacle> getObstacles() {
        return getEntities(Obstacle.class);
    }

    public List<Enemy> getEnemyGroups() {
        List<Enemy> groups = new ArrayList<>();

        for (Enemy enemy : getEnemies()) {
            if (groups.stream().noneMatch(g -> g.getId().equals(enemy.getId()))) {
                groups.add(enemy);
            }
        }

        return groups;
    }


    enum EncounterState {
        ONGOING,
        COMPLETED,
        FAILED,
    }
}
