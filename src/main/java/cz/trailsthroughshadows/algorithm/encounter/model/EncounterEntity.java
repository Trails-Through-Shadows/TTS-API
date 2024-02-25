package cz.trailsthroughshadows.algorithm.encounter.model;

import cz.trailsthroughshadows.api.table.effect.model.Effect;
import lombok.Data;

import java.util.HashMap;

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


    private final HashMap<Effect, Integer> activeEffects = new HashMap<>();

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

    public enum EntityType {
        CHARACTER,
        ENEMY,
        SUMMON,
        OBSTACLE
    }
}
