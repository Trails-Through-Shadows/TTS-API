package cz.trailsthroughshadows.algorithm.encounter.model;

import cz.trailsthroughshadows.api.table.action.features.summon.model.Summon;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class EncounterEntity<T> {

    // id of the entity in the encounter
    private Integer id;
    // only used for enemies, summons and obstacles - the actual type from the database
    private Integer idGroup;

    private int health;
    private int initiative;

    private EntityType type;

    private T entity;


    private final List<EncounterEffect> effects = new ArrayList<>();
    private final List<EncounterEntity<Summon>> summons = new ArrayList<>();

    public EncounterEntity(Integer id, int initiative, int health, EntityType type, T entity) {
        this.id = id;
        this.initiative = initiative;
        this.entity = entity;
        this.type = type;
        this.health = health;
    }

    public EncounterEntity(Integer idEntity, Integer idGroup, int initiative, int health, EntityType type, T entity) {
        this.id = idEntity;
        this.idGroup = idGroup;
        this.initiative = initiative;
        this.entity = entity;
        this.type = type;
        this.health = health;
    }

    public void addEffect(EncounterEffect effect) {
        log.trace("Adding effect '{}'", effect);
        effects.add(effect);
    }
    public void addEffects(List<EncounterEffect> effects) {
        if (effects.isEmpty()) {
            return;
        }
        log.trace("Adding effects {}", effects);
        this.effects.addAll(effects);
    }
    public void damage(int damage) {
        log.trace("Dealing {} damage to entity '{}'", damage, this);
        health -= damage;
    }
    public void applyEffect(EncounterEffect effect) {
        // TODO logic for applying the effect goes here
        if (!effect.isApplicableAtStartTurn())
            return;

        log.trace("Applying effect '{}' for entity '{}'", effect, this);
    }
    public void decreaseEffectDuration(EncounterEffect effect) {
        if (effect.isInfinite())
            return;

        log.trace("Decreasing effect duration '{}'", effect);
        if (effect.getDuration() > 0)
            effect.setDuration(effect.getDuration() - 1);

        if (effect.getDuration() == 0) {
            log.trace("Removing effect");
            effects.remove(effect);
        }
    }

    public void startTurn() {
        log.trace("Starting turn for entity '{}'", entity);
        effects.forEach(this::applyEffect);

        // todo add summons
    }
    public void endTurn() {
        log.trace("Ending turn for entity '{}'", entity);
        effects.forEach(this::decreaseEffectDuration);

        // todo add summons
    }

    @Override
    public String toString() {
        return "%s %s: %s".formatted(type, id, entity.toString());
    }

    public enum EntityType {
        CHARACTER,
        ENEMY,
        SUMMON,
        OBSTACLE
    }
}
