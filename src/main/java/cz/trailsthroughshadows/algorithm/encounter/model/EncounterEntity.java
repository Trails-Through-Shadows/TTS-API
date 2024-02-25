package cz.trailsthroughshadows.algorithm.encounter.model;

import cz.trailsthroughshadows.api.table.effect.model.Effect;
import lombok.Data;

import java.util.HashMap;

@Data
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
    // id of the part the entity is on
    // redudant enemy has id part in his hex
    private Integer idPart;

    public EncounterEntity(Integer idEntity, int initiative, EntityType type, Integer idPart, T entity) {
        this.idEntity = idEntity;
        this.initiative = initiative;
        this.entity = entity;
        this.type = type;
        this.idPart = idPart;
    }

    public EncounterEntity(Integer idEntity, Integer idType, int initiative, Integer idPart, EntityType type, T entity) {
        this.idEntity = idEntity;
        this.idType = idType;
        this.initiative = initiative;
        this.entity = entity;
        this.type = type;
        this.idPart = idPart;
    }

    public enum EntityType {
        CHARACTER,
        ENEMY,
        SUMMON,
        OBSTACLE
    }
}
