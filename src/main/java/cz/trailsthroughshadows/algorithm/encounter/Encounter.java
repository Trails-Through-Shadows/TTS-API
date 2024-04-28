package cz.trailsthroughshadows.algorithm.encounter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.encounter.model.*;
import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.campaign.model.CampaignLocation;
import cz.trailsthroughshadows.api.table.campaign.model.Story;
import cz.trailsthroughshadows.api.table.effect.relation.forcharacter.ClazzEffect;
import cz.trailsthroughshadows.api.table.effect.relation.forcharacter.RaceEffect;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.playerdata.adventure.model.Adventure;
import cz.trailsthroughshadows.api.table.playerdata.character.model.Character;
import cz.trailsthroughshadows.api.table.schematic.location.model.Location;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDoorDTO;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
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
    List<CampaignLocation.Condition> conditions = new ArrayList<>();
    List<Story> stories = new ArrayList<>();

    @Setter
    private ValidationService validation;

    public Encounter(Integer id, Integer idLicense, Adventure adventure, Location location) {
        log.info("Creating encounter {}", id);

        this.id = id;
        this.idLicense = idLicense;
        this.adventure = adventure;
        this.location = location;

        parts = location.getMappedParts();
        startPart = location.getStartPart();

        conditions = adventure.getCampaign().getConditions(location.getId());
        stories = adventure.getCampaign().getStories(location.getId());

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

        initConditions();
        discoverPart(startPart.getId());
    }

    private void discoverPart(Integer idPart) {
        Part part = parts.stream()
                .filter(p -> p.getId().equals(idPart))
                .findFirst()
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Part {} not found", idPart));

        if (part.getUnlocked()) {
            logError(HttpStatus.NOT_FOUND, "Part %s already unlocked".formatted(idPart));
        }

        part.unlock();
        progressCondition(CampaignLocation.Condition.Type.DOORS_OPENED);
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

    // initiative
    public void rollInitiative(List<Initiative> initiatives) {
        log.info("Rolling initiative");

        if (state != EncounterState.NEW) {
            logError(HttpStatus.BAD_REQUEST, "Initiative already rolled.");
        }

        if (initiatives.size() != entities.getCharacters().size()) {
            logError(HttpStatus.NOT_ACCEPTABLE, "Initiative size does not match number of characters.");
        }

        if (initiatives.stream().map(Initiative::getId).distinct().count() != initiatives.size()) {
            logError(HttpStatus.NOT_ACCEPTABLE, "Initiative contains duplicate ids.");
        }

        for (Initiative initiative : initiatives) {
            EncounterEntity<Character> character = entities.getCharacter(initiative.getId());
            log.debug("Setting initiative for Character #{}", initiative.getId());
            character.setInitiative(character.getEntity().getInitiative() + initiative.getInitiative());
            log.trace("Initiative set to {} {}{} = {}", character.getEntity().getInitiative(),
                    initiative.getInitiative() >= 0 ? "+" : "", initiative.getInitiative(), character.getInitiative());
        }

        state = EncounterState.ONGOING;
    }

    public LinkedHashMap<String, Object> getInitiative() {
        if (state != EncounterState.ONGOING) {
            logError(HttpStatus.BAD_REQUEST, "Roll initiative first.");
        }

        LinkedHashMap<String, Object> ret = new LinkedHashMap<>();
        List<Initiative> initiatives = new ArrayList<>();

        for (EncounterEntity<Character> character : entities.getCharacters()) {
            initiatives.add(
                    new Initiative(character.getId(), character.getInitiative(), EncounterEntity.EntityType.CHARACTER));
        }

        for (EncounterEntity<Enemy> enemy : entities.getEnemyGroups()) {
            initiatives
                    .add(new Initiative(enemy.getIdGroup(), enemy.getInitiative(), EncounterEntity.EntityType.ENEMY));
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

        ret.put("initiatives", initiatives);

        if (entities.isEntityActive()) {
            LinkedHashMap<String, Object> active = new LinkedHashMap<>();
            active.put("type", entities.getActiveEntity().getType());
            active.put("id", entities.getActiveEntity().getId());
            if (entities.getActiveEntity().getType().equals(EncounterEntity.EntityType.ENEMY)) {
                active.put("action", entities.getActiveEntity().getAction());
            }
            ret.put("active", active);
        } else {
            ret.put("active", null);
        }

        return ret;
    }

    // start turn
    private List<EntityStatusUpdate> startTurn(EncounterEntity.EntityType type, Integer id) {
        log.info("Starting turn for {} {}", type, id);

        if (state != EncounterState.ONGOING) {
            logError(HttpStatus.BAD_REQUEST, "Encounter not ongoing.");
        }

        if (!entities.canHaveTurn(type)) {
            logError(HttpStatus.NOT_ACCEPTABLE, "This type of entity can't have a turn.");
        }

        if (entities.isEntityActive()) {
            logError(HttpStatus.NOT_ACCEPTABLE, "Another entity is already active.");
        }

        entities.setActiveEntity(type, id);

        List<EntityStatusUpdate> ret = new ArrayList<>();

        if (type.equals(EncounterEntity.EntityType.CHARACTER)) {
            EncounterEntity<Character> character = entities.getCharacter(id);
            character.startTurn();
            ret.add(character.getStatusUpdate());
            checkEntityDead(character);
        }

        if (type.equals(EncounterEntity.EntityType.ENEMY)) {
            List<EncounterEntity<Enemy>> enemies = entities.getEnemyGroup(id);

            if (enemies.isEmpty()) {
                logError(HttpStatus.NOT_FOUND, "No enemy in group {}, something went wrong.", id);
            }

            for (EncounterEntity<Enemy> enemy : enemies) {
                enemy.startTurn();
                ret.add(enemy.getStatusUpdate());
                checkEntityDead(enemy);
            }
        }

        // check condition
        state = checkConditions();

        return ret;
    }

    public EntityStatusUpdate startCharacterTurn(Integer id) {
        return startTurn(EncounterEntity.EntityType.CHARACTER, id).stream().findFirst()
                .orElseThrow(() -> logErrorReturn(HttpStatus.NOT_FOUND, "Couldn't find character #{}", id));
    }

    public LinkedHashMap<String, Object> startEnemyTurn(Integer id) {
        LinkedHashMap<String, Object> ret = new LinkedHashMap<>();

        ActionDTO action = new ActionDTO(entities.getEnemyGroup(id).getFirst().getEntity().drawCard());
        Initialization.hibernateInitializeAll(action);
        ret.put("action", action);

        ret.put("entities", startTurn(EncounterEntity.EntityType.ENEMY, id));

        if (entities.isEntityActive())
            entities.getActiveEntity().setAction(action);

        return ret;
    }

    // end turn
    private List<EntityStatusUpdate> endTurn(EncounterEntity.EntityType type, Integer id) {
        log.info("Ending turn for {} {}", type, id);

        if (state != EncounterState.ONGOING) {
            logError(HttpStatus.BAD_REQUEST, "Encounter not ongoing.");
        }

        if (!entities.canHaveTurn(type)) {
            logError(HttpStatus.NOT_ACCEPTABLE, "This type of entity can't have a turn.");
        }

        if (!entities.isEntityActive()) {

            logError(HttpStatus.NOT_ACCEPTABLE, "Can't end turn - no active entity.");
        }

        if (!(entities.getActiveEntity().getType().equals(type) && entities.getActiveEntity().getId().equals(id))) {
            logError(HttpStatus.NOT_ACCEPTABLE, "Can't end turn - another entity is active.");
        }

        List<EntityStatusUpdate> ret = new ArrayList<>();

        if (type.equals(EncounterEntity.EntityType.CHARACTER)) {
            EncounterEntity<Character> character = entities.getCharacter(id);
            character.endTurn();
            ret.add(character.getStatusUpdate());
            checkEntityDead(character);
        }

        if (type.equals(EncounterEntity.EntityType.ENEMY)) {
            List<EncounterEntity<Enemy>> enemies = entities.getEnemyGroup(id);
            for (EncounterEntity<Enemy> enemy : enemies) {
                enemy.endTurn();
                ret.add(enemy.getStatusUpdate());
                checkEntityDead(enemy);
            }
        }

        entities.resetActiveEntity();

        return ret;
    }

    public EntityStatusUpdate endCharacterTurn(Integer id) {
        return endTurn(EncounterEntity.EntityType.CHARACTER, id).stream().findFirst()
                .orElseThrow(() -> logErrorReturn(HttpStatus.NOT_FOUND, "Couldn't find character #{}", id));
    }

    public List<EntityStatusUpdate> endEnemyTurn(Integer id) {
        return endTurn(EncounterEntity.EntityType.ENEMY, id);
    }

    // interaction
    private EntityStatusUpdate entityInteraction(EncounterEntity<?> entity, int damage, List<EncounterEffect> effects) {
        log.info("Interacting with entity '{}'", entity);

        if (state != EncounterState.ONGOING) {
            logError(HttpStatus.BAD_REQUEST, "Encounter not ongoing.");
        }

        if (damage < 0) {
            throw new RestException(new RestError(HttpStatus.NOT_ACCEPTABLE, "Damage must not be lesser than 0.",
                    "damage", damage));
        } else if (damage > 50) {
            throw new RestException(new RestError(HttpStatus.NOT_ACCEPTABLE,
                    "You just woke up and chose violence. (Damage must not be greater than 50.)",
                    "damage", damage));
        }

        if (effects == null) {
            effects = new ArrayList<>();
        }

        for (EncounterEffect effect : effects) {
            validation.validate(effect.toEffect());
        }

        entity.addEffects(effects);
        entity.damage(damage, EncounterEntity.DamageSource.ATTACK);

        EntityStatusUpdate ret = entity.getStatusUpdate();

        checkEntityDead(entity);

        // check condition
        state = checkConditions();

        return ret;
    }

    private void checkEntityDead(EncounterEntity<?> entity) {
        if (entity.getHealth() == 0) {
            log.info("Entity '{}' is dead", entity);

            if (entity.getType().equals(EncounterEntity.EntityType.CHARACTER)) {
                progressCondition(CampaignLocation.Condition.Type.PLAYER_DEATHS);
            }
            if (entity.getType().equals(EncounterEntity.EntityType.ENEMY)) {
                progressCondition(CampaignLocation.Condition.Type.ENEMY_DEATHS);
            }

            // to make sure the entity doesn't stay active after it's dead
            int entityId = entity.getType() == EncounterEntity.EntityType.CHARACTER ? entity.getId()
                    : entity.getIdGroup();
            if (entities.isEntityActive() && entities.getActiveEntity().getType() == entity.getType()
                    && entities.getActiveEntity().getId() == entityId) {
                if (entity.getType().equals(EncounterEntity.EntityType.CHARACTER)
                        || entity.getType().equals(EncounterEntity.EntityType.ENEMY) && entities
                                .getEnemyGroup(entity.getIdGroup()).stream().allMatch(e -> e.getHealth() == 0)) {
                    log.trace("Resetting active entity - they died during their turn");
                    entities.resetActiveEntity();
                }
            }

            entities.removeEntity(entity);
        }
    }

    public EntityStatusUpdate characterInteraction(Integer id, Interaction interaction) {
        return entityInteraction(entities.getCharacter(id), interaction.getDamage(), interaction.getEffects());
    }

    public EntityStatusUpdate enemyInteraction(Integer id, Integer idGroup, Interaction interaction) {
        return entityInteraction(entities.getEnemy(id, idGroup), interaction.getDamage(), interaction.getEffects());
    }

    public EntityStatusUpdate obstacleInteraction(Integer id, Integer idGroup, Interaction interaction) {
        return entityInteraction(entities.getObstacle(id, idGroup), interaction.getDamage(), interaction.getEffects());
    }

    public EntityStatusUpdate summonInteraction(Integer id, Integer idGroup, Interaction interaction) {
        return entityInteraction(entities.getSummon(id, idGroup), interaction.getDamage(), interaction.getEffects());
    }

    // door
    public void openDoor(LocationDoorDTO door) {
        log.info("Opening door {}", door);
        // todo more sophisticated door opening

        if (state != EncounterState.ONGOING) {
            logError(HttpStatus.BAD_REQUEST, "Encounter not ongoing.");
        }
        if (doorsToOpen.contains(door)) {
            logError(HttpStatus.NOT_ACCEPTABLE, "Door already opened.");
        }

        doorsToOpen.add(door);
    }

    // round
    public LinkedHashMap<String, Object> endRound() {
        log.info("Ending round");

        LinkedHashMap<String, Object> ret = new LinkedHashMap<>();

        if (state != EncounterState.ONGOING) {
            logError(HttpStatus.BAD_REQUEST, "Encounter not ongoing.");
        }

        if (entities.isEntityActive()) {
            logError(HttpStatus.NOT_ACCEPTABLE, "Can't end round - an entity is still active.");
        }

        // check opened doors
        List<Integer> unlockedParts = new ArrayList<>();
        for (LocationDoorDTO door : doorsToOpen) {
            log.trace("Checking door {}", door);
            Part partTo = parts.stream()
                    .filter(p -> p.getId().equals(door.getKey().getIdPartTo()))
                    .findFirst()
                    .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Part {} not found",
                            door.getKey().getIdPartTo()));

            discoverPart(partTo.getId());
            unlockedParts.add(partTo.getId());
        }
        doorsToOpen.clear();

        // add unlocked parts to ret as json array
        ret.put("unlockedParts", unlockedParts);

        // check condition
        progressCondition(CampaignLocation.Condition.Type.ROUND_REACHED);
        state = checkConditions();

        // status
        ret.put("status", state);

        return ret;
    }

    // condition
    private void progressCondition(CampaignLocation.Condition.Type type) {
        conditions.stream()
                .filter(c -> c.getType().equals(type))
                .forEach((c) -> {
                    c.progress();
                    log.debug("Progressing condition {} to {}", c.getType(), c.getProgression());
                });
    }

    private boolean checkEntityCondition(List<?> entities, CampaignLocation.Condition condition, String entityName) {
        if (condition.getProgression() >= condition.getValue()) {
            log.debug("Condition reached: %s %s dead".formatted(condition.getValue(), entityName));
            log.info("Setting state to %s".formatted(condition.getResult()));
            return true;
        }
        return false;
    }

    private void initConditions() {
        for (CampaignLocation.Condition condition : conditions) {
            if (condition.getValue() != -1)
                continue;

            switch (condition.getType()) {
                case ENEMY_DEATHS -> {
                    int count = parts.stream().mapToInt(p -> p.getEnemies().size()).sum();
                    condition.setValue(count);
                }
                case PLAYER_DEATHS -> {
                    int count = entities.getCharacters().size();
                    condition.setValue(count);
                }
                case DOORS_OPENED -> {
                    int count = parts.size();
                    condition.setValue(count);
                }
                case ROUND_REACHED -> {
                    condition.setValue(0);
                }
            }
        }
    }

    private EncounterState checkConditions() {
        if (state != EncounterState.ONGOING) {
            logError(HttpStatus.BAD_REQUEST, "Encounter not ongoing.");
        }

        for (CampaignLocation.Condition condition : conditions) {
            switch (condition.getType()) {
                case ENEMY_DEATHS -> {
                    if (checkEntityCondition(entities.getEnemies(), condition, "enemies"))
                        return condition.getResult();
                }
                case PLAYER_DEATHS -> {
                    if (checkEntityCondition(entities.getCharacters(), condition, "players"))
                        return condition.getResult();
                }
                case DOORS_OPENED -> {
                    if (checkEntityCondition(parts.stream().filter(Part::getUnlocked).toList(), condition, "parts"))
                        return condition.getResult();
                }
                case ROUND_REACHED -> {
                    if (condition.getProgression() >= condition.getValue()) {
                        log.info(("Condition reached: Round %s reached").formatted(condition.getValue()));
                        log.info("Setting state to %s".formatted(condition.getResult()));
                        return condition.getResult();
                    }
                }
            }
        }

        return EncounterState.ONGOING;
    }

    public Story getStory() {
        return stories.stream()
                .filter(s -> s.getTrigger().equals(state))
                .findFirst()
                .orElse(Story.about(state));
    }

    // misc
    private void endEncounter() {
        log.info("Ending encounter");
        state = EncounterState.COMPLETED;
    }

    private void logError(HttpStatus status, String message, Object... args) {
        throw logErrorReturn(status, message, args);
    }

    private RestException logErrorReturn(HttpStatus status, String message, Object... args) {
        log.error(message, args);
        return RestException.of(status, message, args);
    }

    public enum EncounterState {
        NEW,
        ONGOING,
        COMPLETED,
        FAILED,
    }
}
