package cz.trailsthroughshadows.algorithm.encounter.model;

import cz.trailsthroughshadows.api.table.effect.model.Effect;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class EncounterEntity<T> {

    // id of the entity in the encounter
    private Integer idEntity;
    // only used for enemies, summons and obstacles - the actual type from the database
    private Integer idType;

    private int initiative;
    private EntityType type;

    private T entity;

    private int health;

    private final HashMap<Effect, Integer> activeEffects = new HashMap<>();

    public EncounterEntity(Integer idEntity, int initiative, EntityType type, T entity) {
        this.idEntity = idEntity;
        this.initiative = initiative;
        this.entity = entity;
        this.type = type;
    }

    public EncounterEntity(Integer idEntity, Integer idType, int initiative, EntityType type, T entity) {
        this.idEntity = idEntity;
        this.idType = idType;
        this.initiative = initiative;
        this.entity = entity;
        this.type = type;
    }

    public enum EntityType {
        CHARACTER,
        ENEMY,
        SUMMON,
        OBSTACLE
    }
}
