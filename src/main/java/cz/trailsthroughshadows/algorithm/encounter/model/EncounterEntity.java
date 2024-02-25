package cz.trailsthroughshadows.algorithm.encounter.model;

import cz.trailsthroughshadows.api.table.action.features.summon.model.Summon;
import cz.trailsthroughshadows.api.table.effect.model.Effect;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
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
        effects.add(effect);
    }
    public void addEffects(List<EncounterEffect> effects) {
        this.effects.addAll(effects);
    }
    public void applyEffect(EncounterEffect effect) {
        // TODO logic for applying the effect goes here
    }
    public void decreaseEffectDuration(EncounterEffect effect) {
        effect.setDuration(effect.getDuration() - 1);
    }

    public void startTurn() {
        effects.forEach(this::applyEffect);
    }
    public void endTurn() {
        effects.forEach(this::decreaseEffectDuration);
    }


    public enum EntityType {
        CHARACTER,
        ENEMY,
        SUMMON,
        OBSTACLE
    }
}
