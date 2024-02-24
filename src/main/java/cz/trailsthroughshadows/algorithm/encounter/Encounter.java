package cz.trailsthroughshadows.algorithm.encounter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.encounter.model.EncounterEntity;
import cz.trailsthroughshadows.algorithm.encounter.model.Initiative;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.table.action.features.summon.model.Summon;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.playerdata.adventure.model.Adventure;
import cz.trailsthroughshadows.api.table.playerdata.character.model.Character;
import cz.trailsthroughshadows.api.table.schematic.location.model.Location;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Data
@AllArgsConstructor
@JsonSerialize(using = EncounterSerializer.class)
public class Encounter {

    private Integer id;
    private Integer idLicense;
    private Adventure adventure;
    private Location location;
    private Part startPart;
    private EncounterState state = EncounterState.ONGOING;
    private List<EncounterEntity<?>> entities = new ArrayList<>();

    public Encounter(Integer id, Integer idLicense, Adventure adventure, Location location) {
        this.id = id;
        this.idLicense = idLicense;
        this.adventure = adventure;
        this.location = location;

        startPart = location.getStartPart();

        //characters.addAll()
        var adv = Optional.of(
                        adventure.getCharacters().stream()
                                .map(Character::fromDTO)
                                .peek(c -> c.setIdPart(startPart.getId()))
                                .map(c -> new EncounterEntity<Character>(c.getId(), c.getInitiative(), EncounterEntity.EntityType.CHARACTER, c.getIdPart(), c)) //TODO get id part in class Character
                                .toList())
                .orElse(new ArrayList<>());
        if (!adv.isEmpty()) {
            entities.addAll(adv);
        } else {
            throw RestException.of(HttpStatus.BAD_REQUEST, "No characters in adventure");
        }


        //enemies.addAll() just on unlocked parts
        Part[] tmp = new Part[1];
        var enem = Optional.ofNullable(
                        location.getMappedParts().stream()
                                .filter(Part::getUnlocked)
                                .peek(part -> tmp[0] = part)
                                .flatMap(p -> p.getEnemies().stream())
                                .map(e -> new EncounterEntity<Enemy>(e.getId(), e.getInitiative(), EncounterEntity.EntityType.ENEMY, tmp[0].getId(), e)))
                .map(s -> s.collect(Collectors.toList()))
                .orElseGet(ArrayList::new);

        if (!enem.isEmpty()) {
            entities.addAll(enem);
        } else {
            log.warn("No enemies in location " + location.getId() + " or no unlocked parts");
        }


        //Sumonny jebat they will not be prespawned

        //obstacles.addAll()
        var obst = Optional.of(
                        location.getMappedParts().stream()
                                .filter(Part::getUnlocked)
                                .map(p -> new EncounterEntity<Part>(p.getId(), 0, EncounterEntity.EntityType.OBSTACLE, p.getId(), p))
                                .toList())
                .orElse(new ArrayList<>());
        if (!obst.isEmpty()) {
            entities.addAll(obst);
        }

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
