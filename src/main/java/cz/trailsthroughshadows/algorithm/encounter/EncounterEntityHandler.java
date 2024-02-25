package cz.trailsthroughshadows.algorithm.encounter;

import cz.trailsthroughshadows.algorithm.encounter.model.EncounterEffect;
import cz.trailsthroughshadows.algorithm.encounter.model.EncounterEntity;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.table.action.features.summon.model.Summon;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.playerdata.character.model.Character;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Slf4j
public class EncounterEntityHandler {
    private final List<EncounterEntity<?>> entities = new ArrayList<>();

    public void removeEntity(EncounterEntity<?> entity) {
        entities.remove(entity);
    }
    public void addEntity(EncounterEntity<?> entity) {
        entities.add(entity);
    }

    private <T> List<EncounterEntity<T>> getEntities(Class<?> c) {
        return entities.stream()
                .filter(e -> e.getEntity().getClass().equals(c))
                .map(e -> (EncounterEntity<T>) e)
                .collect(Collectors.toList());
    }

    // Character
    public void addCharacter(Character character) {
        addCharacter(character, new ArrayList<>());
    }
    public void addCharacter(Character character, List<EncounterEffect> effects) {
        log.trace("Adding character '{}' with {} effects", character, effects.size());
        EncounterEntity<Character> entity = new EncounterEntity<Character>(character.getId(), character.getInitiative(), character.getHealth(),
                EncounterEntity.EntityType.CHARACTER, character);
        entity.addEffects(effects);
        entities.add(entity);
    }
    public List<EncounterEntity<Character>> getCharacters() {
        return getEntities(Character.class);
    }
    public EncounterEntity<Character> getCharacter(int id) {
        return getCharacters().stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Character with id " + id + " not found"));
    }
    public void removeCharacter(int id) {
        log.trace("Removing character with id {}", id);
        entities.removeIf(e -> e.getType().equals(EncounterEntity.EntityType.CHARACTER) && e.getId().equals(id));
    }

    // Enemy
    public void addEnemy(Enemy enemy) {
        addEnemy(enemy, new ArrayList<>());
    }
    public void addEnemy(Enemy enemy, List<EncounterEffect> effects) {
        log.trace("Adding enemy '{}' with {} effects", enemy, effects.size());
        EncounterEntity<Enemy> entity = new EncounterEntity<Enemy>(getNextId(EncounterEntity.EntityType.ENEMY, enemy.getId()), enemy.getId(), enemy.getBaseInitiative(), enemy.getBaseHealth(),
                EncounterEntity.EntityType.ENEMY, enemy);
        entity.addEffects(effects);
        entities.add(entity);
    }
    public List<EncounterEntity<Enemy>> getEnemies() {
        return getEntities(Enemy.class);
    }
    public List<Enemy> getEnemyGroups() {
        List<Enemy> groups = new ArrayList<>();

        for (EncounterEntity<Enemy> enemy : getEnemies()) {
            if (groups.stream().noneMatch(g -> g.getId().equals(enemy.getEntity().getId()))) {
                groups.add(enemy.getEntity());
            }
        }

        return groups;
    }
    public EncounterEntity<Enemy> getEnemy(int id, int idGroup) {
        return getEnemies().stream()
                .filter(e -> e.getId().equals(id) && e.getIdGroup().equals(idGroup))
                .findFirst()
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Enemy with id " + id + " and idGroup " + idGroup + " not found"));
    }
    public void removeEnemy(int id, int idGroup) {
        log.trace("Removing enemy with id {} and idGroup {}", id, idGroup);
        entities.removeIf(e -> e.getType().equals(EncounterEntity.EntityType.ENEMY) && e.getId().equals(id) && e.getIdGroup().equals(idGroup));
    }

    // Summon
    public void addSummon(Summon summon) {
        addSummon(summon, new ArrayList<>());
    }
    public void addSummon(Summon summon, List<EncounterEffect> effects) {
        log.trace("Adding summon '{}' with {} effects", summon, effects.size());
        EncounterEntity<Summon> entity = new EncounterEntity<Summon>(getNextId(EncounterEntity.EntityType.SUMMON, summon.getId()), summon.getId(), -1, summon.getHealth(),
                EncounterEntity.EntityType.SUMMON, summon);
        entity.addEffects(effects);
        entities.add(entity);
    }
    public List<EncounterEntity<Summon>> getSummons() {
        return getEntities(Summon.class);
    }
    public EncounterEntity<Summon> getSummon(int id, int idGroup) {
        return getSummons().stream()
                .filter(e -> e.getId().equals(id) && e.getIdGroup().equals(idGroup))
                .findFirst()
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Summon with id " + id + " and idGroup " + idGroup + " not found"));
    }
    public void removeSummon(int id, int idGroup) {
        log.trace("Removing summon with id {} and idGroup {}", id, idGroup);
        entities.removeIf(e -> e.getType().equals(EncounterEntity.EntityType.SUMMON) && e.getId().equals(id) && e.getIdGroup().equals(idGroup));
    }

    // Obstacle
    public void addObstacle(Obstacle obstacle) {
        addObstacle(obstacle, new ArrayList<>());
    }
    public void addObstacle(Obstacle obstacle, List<EncounterEffect> effects) {
        log.trace("Adding obstacle '{}' with {} effects", obstacle, effects.size());
        EncounterEntity<Obstacle> entity = new EncounterEntity<Obstacle>(getNextId(EncounterEntity.EntityType.OBSTACLE, obstacle.getId()), obstacle.getId(), -1, obstacle.getBaseHealth(),
                EncounterEntity.EntityType.OBSTACLE, obstacle);
        entity.addEffects(effects);
        entities.add(entity);
    }
    public List<EncounterEntity<Obstacle>> getObstacles() {
        return getEntities(Obstacle.class);
    }
    public EncounterEntity<Obstacle> getObstacle(int id, int idGroup) {
        return getObstacles().stream()
                .filter(e -> e.getId().equals(id) && e.getIdGroup().equals(idGroup))
                .findFirst()
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Obstacle with id " + id + " and idGroup " + idGroup + " not found"));
    }
    public void removeObstacle(int id, int idGroup) {
        log.trace("Removing obstacle with id {} and idGroup {}", id, idGroup);
        entities.removeIf(e -> e.getType().equals(EncounterEntity.EntityType.OBSTACLE) && e.getId().equals(id) && e.getIdGroup().equals(idGroup));
    }

    private int getNextId(EncounterEntity.EntityType type, Integer idGroup) {
        for (int i = 1; i < entities.size() + 1; i++) {
            int finalI = i;
            if (entities.stream().noneMatch(e -> e.getId().equals(finalI) && e.getType().equals(type) && e.getIdGroup().equals(idGroup))) {
                return i;
            }
        }
        return entities.size() + 1;
    }
}
