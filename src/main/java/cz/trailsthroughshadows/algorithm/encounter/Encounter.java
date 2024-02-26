package cz.trailsthroughshadows.algorithm.encounter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.encounter.model.EncounterEffect;
import cz.trailsthroughshadows.algorithm.encounter.model.EncounterEntity;
import cz.trailsthroughshadows.algorithm.encounter.model.Initiative;
import cz.trailsthroughshadows.algorithm.encounter.model.Interaction;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.table.effect.relation.forcharacter.ClazzEffect;
import cz.trailsthroughshadows.api.table.effect.relation.forcharacter.RaceEffect;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.playerdata.adventure.model.Adventure;
import cz.trailsthroughshadows.api.table.playerdata.character.model.Character;
import cz.trailsthroughshadows.api.table.schematic.location.model.Location;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDoorDTO;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

    private EncounterState state = EncounterState.NEW;

    private EncounterEntityHandler entities = new EncounterEntityHandler();

    private Part startPart;
    private List<Part> parts;
    private List<LocationDoorDTO> doorsToOpen = new ArrayList<>();

    public Encounter(Integer id, Integer idLicense, Adventure adventure, Location location) {
        this.id = id;
        this.idLicense = idLicense;
        this.adventure = adventure;
        this.location = location;

        log.info("Creating encounter {}", id);

        parts = location.getMappedParts();
        startPart = location.getStartPart();

        if (startPart == null) {
            log.warn("No unlocked parts in location " + location.getId());
        }

        for (Character character : adventure.getCharacters().stream().map(Character::fromDTO).toList()) {
            List<EncounterEffect> effects = character.getRace().getEffects().stream()
                    .map(RaceEffect::getEffect)
                    .map(EncounterEffect::fromEffect)
                    .collect(Collectors.toCollection(ArrayList::new));
            effects.addAll(character.getClazz().getEffects().stream()
                    .map(ClazzEffect::getEffect)
                    .map(EncounterEffect::fromEffect)
                    .toList());
            entities.addCharacter(character, effects);
        }

        discoverPart(startPart.getId());
    }

    private void discoverPart(Integer idPart) {
        Part part = parts.stream()
                .filter(p -> p.getId().equals(idPart))
                .findFirst()
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Part {} not found", idPart));

        if (part.getUnlocked()) {
            throw RestException.of(HttpStatus.NOT_FOUND, "Part {} already unlocked", idPart);
        }

        part.unlock();
        log.info("Unlocking part {}", idPart);

        // add enemies
        List<Enemy> enemies = part.getEnemies();
        log.info("Adding enemies: {}", enemies.size());
        for (Enemy enemy : enemies) {
            List<EncounterEffect> effects = enemy.getMappedEffects().stream()
                    .map(EncounterEffect::fromEffect).toList();
            entities.addEnemy(enemy, effects);
        }

        // add obstacles
        List<Obstacle> obstacles = part.getObstacles();
        log.info("Adding obstacles: {}", obstacles.size());
        for (Obstacle obstacle : obstacles) {
            List<EncounterEffect> effects = obstacle.getMappedEffects().stream()
                    .map(EncounterEffect::fromEffect).toList();
            entities.addObstacle(obstacle);
        }
    }

    public void rollInitiative(List<Initiative> initiatives) {
        if (state != EncounterState.NEW) {
            throw RestException.of(HttpStatus.BAD_REQUEST, "Initiative already rolled.");
        }

        if (initiatives.size() != entities.getCharacters().size()) {
            throw RestException.of(HttpStatus.NOT_ACCEPTABLE, "Initiative size does not match number of characters.");
        }

        if (initiatives.stream().map(Initiative::getId).distinct().count() != initiatives.size()) {
            throw RestException.of(HttpStatus.NOT_ACCEPTABLE, "Initiative contains duplicate ids.");
        }

        for (Initiative initiative : initiatives) {
            log.info("Setting initiative for id {} to {}", initiative.getId(), initiative.getInitiative());
            EncounterEntity<Character> character = entities.getCharacter(initiative.getId());
            character.setInitiative(character.getEntity().getInitiative() + initiative.getInitiative());
            log.debug("Initiative set to {}", character.getInitiative());
        }

        state = EncounterState.ONGOING;
    }

    public List<Initiative> getInitiative() {
        if (state != EncounterState.ONGOING) {
            throw RestException.of(HttpStatus.BAD_REQUEST, "Roll initiative first.");
        }

        List<Initiative> initiatives = new ArrayList<>();

        for (EncounterEntity<Character> character : entities.getCharacters()) {
            initiatives.add(new Initiative(character.getId(), character.getInitiative(), EncounterEntity.EntityType.CHARACTER));
        }

        for (EncounterEntity<Enemy> enemy : entities.getEnemyGroups()) {
            initiatives.add(new Initiative(enemy.getIdGroup(), enemy.getInitiative(), EncounterEntity.EntityType.ENEMY));
        }

        // sort initiatives by initiative, players go before enemies if there is a tie
        initiatives.sort((i1, i2) -> {
            int initiative = i2.getInitiative().compareTo(i1.getInitiative());
            if (initiative != 0) {
                return initiative;
            }

            if (i1.getType().equals(EncounterEntity.EntityType.CHARACTER)) {
                return -1;
            }

            return 1;
        });

        return initiatives;
    }

    private void startTurn(EncounterEntity.EntityType type, Integer id) {
        log.info("Starting turn for {} {}", type, id);

        if (state != EncounterState.ONGOING) {
            throw RestException.of(HttpStatus.BAD_REQUEST, "Encounter not ongoing.");
        }

        if (!entities.canHaveTurn(type)) {
            throw RestException.of(HttpStatus.NOT_ACCEPTABLE, "This type of entity can't have a turn.");
        }

        if (entities.isEntityActive()) {
            throw RestException.of(HttpStatus.NOT_ACCEPTABLE, "Another entity is already active.");
        }

        entities.setActiveEntity(type, id);

        if (type.equals(EncounterEntity.EntityType.CHARACTER)) {
            EncounterEntity<Character> character = entities.getCharacter(id);
            character.startTurn();
        }

        if (type.equals(EncounterEntity.EntityType.ENEMY)) {
            List<EncounterEntity<Enemy>> enemies = entities.getEnemyGroup(id);
            for (EncounterEntity<Enemy> enemy : enemies) {
                enemy.startTurn();
            }
        }
    }
    public void startCharacterTurn(Integer id) {
        startTurn(EncounterEntity.EntityType.CHARACTER, id);
    }
    public void startEnemyTurn(Integer id) {
        startTurn(EncounterEntity.EntityType.ENEMY, id);
    }

    private void endTurn(EncounterEntity.EntityType type, Integer id) {
        log.info("Ending turn for {} {}", type, id);

        if (state != EncounterState.ONGOING) {
            throw RestException.of(HttpStatus.BAD_REQUEST, "Encounter not ongoing.");
        }

        if (!entities.canHaveTurn(type)) {
            throw RestException.of(HttpStatus.NOT_ACCEPTABLE, "This type of entity can't have a turn.");
        }

        if (!entities.isEntityActive()) {
            throw RestException.of(HttpStatus.NOT_ACCEPTABLE, "Can't end turn - no active entity.");
        }

        if (!(entities.getActiveEntity().getType().equals(type) && entities.getActiveEntity().getId().equals(id))) {
            throw RestException.of(HttpStatus.NOT_ACCEPTABLE, "Can't end turn - another entity is active.");
        }

        Initiative activeEntity = entities.getActiveEntity();

        if (type.equals(EncounterEntity.EntityType.CHARACTER)) {
            EncounterEntity<Character> character = entities.getCharacter(id);
            character.endTurn();
        }

        if (type.equals(EncounterEntity.EntityType.ENEMY)) {
            List<EncounterEntity<Enemy>> enemies = entities.getEnemyGroup(id);
            for (EncounterEntity<Enemy> enemy : enemies) {
                enemy.endTurn();
            }
        }

        entities.resetActiveEntity();
    }
    public void endCharacterTurn(Integer id) {
        endTurn(EncounterEntity.EntityType.CHARACTER, id);
    }
    public void endEnemyTurn(Integer id) {
        endTurn(EncounterEntity.EntityType.ENEMY, id);
    }

    private void entityInteraction(EncounterEntity<?> entity, int damage, List<EncounterEffect> effects) {
        log.info("Interacting with entity '{}'", entity);

        if (state != EncounterState.ONGOING) {
            throw RestException.of(HttpStatus.BAD_REQUEST, "Encounter not ongoing.");
        }

        entity.damage(damage);
        entity.addEffects(effects);
    }

    public void characterInteraction(Integer id, Interaction interaction) {
        entityInteraction(entities.getCharacter(id), interaction.getDamage(), interaction.getEffects());
    }
    public void enemyInteraction(Integer id, Integer idGroup, Interaction interaction) {
        entityInteraction(entities.getEnemy(id, idGroup), interaction.getDamage(), interaction.getEffects());
    }
    public void obstacleInteraction(Integer id, Integer idGroup, Interaction interaction) {
        entityInteraction(entities.getObstacle(id, idGroup), interaction.getDamage(), interaction.getEffects());
    }
    public void summonInteraction(Integer id, Integer idGroup, Interaction interaction) {
        entityInteraction(entities.getSummon(id, idGroup), interaction.getDamage(), interaction.getEffects());
    }


    public LinkedHashMap<String, Object> endRound() {
        log.info("Ending round");

        LinkedHashMap<String, Object> ret = new LinkedHashMap<>();

        if (state != EncounterState.ONGOING) {
            throw RestException.of(HttpStatus.BAD_REQUEST, "Encounter not ongoing.");
        }

        if (entities.isEntityActive()) {
            throw RestException.of(HttpStatus.NOT_ACCEPTABLE, "Can't end round - an entity is still active.");
        }

        // check opened doors
        List<Integer> unlockedParts = new ArrayList<>();
        for (LocationDoorDTO door : doorsToOpen) {
            log.trace("Checking door {}", door);
            Part partTo = parts.stream()
                            .filter(p -> p.getId().equals(door.getIdPartTo()))
                            .findFirst()
                            .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Part {} not found", door.getIdPartTo()));

            if (partTo.getUnlocked()) {
                log.trace("Part {} already unlocked", partTo.getId());
                continue;
            }

            log.trace("Unlocking part {}", partTo.getId());
            partTo.unlock();
            unlockedParts.add(partTo.getId());
        }
        doorsToOpen.clear();

        // add unlocked parts to ret as json array
        ret.put("unlockedParts", unlockedParts);

        // check win condition
        // todo

        // status
        ret.put("status", state);

        return ret;
    }

    enum EncounterState {
        NEW,
        ONGOING,
        COMPLETED,
        FAILED,
    }
}
