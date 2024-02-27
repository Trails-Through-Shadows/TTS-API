package cz.trailsthroughshadows.algorithm.encounter.model;

import cz.trailsthroughshadows.api.table.action.features.summon.model.Summon;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
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
    private int defence;
    private int initiative;

    private EntityType type;

    private T entity;


    private final List<EncounterEffect> effects = new ArrayList<>();
    private final List<EncounterEntity<Summon>> summons = new ArrayList<>();

    public EncounterEntity(Integer id, int initiative, int health, int defence, EntityType type, T entity) {
        this.id = id;
        this.initiative = initiative;
        this.entity = entity;
        this.type = type;
        this.health = health;
        this.defence = defence;
    }

    public EncounterEntity(Integer idEntity, Integer idGroup, int initiative, int health, int defence, EntityType type, T entity) {
        this.id = idEntity;
        this.idGroup = idGroup;
        this.initiative = initiative;
        this.entity = entity;
        this.type = type;
        this.health = health;
        this.defence = defence;
    }

    public void addEffect(EncounterEffect effect) {
        log.trace("Adding effect '{}'", effect);

        List<EncounterEffect> resistances = effects.stream()
                .filter(e -> e.getType().equals(EncounterEffect.getResistanceType(effect)))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (!resistances.isEmpty()) {
            log.trace("Entity has resistance: {}", resistances);
            int resistance = resistances.stream()
                    .mapToInt(EncounterEffect::getStrength)
                    .max()
                    .orElse(0);

            effect.decreaseStrength(resistance);

            if (effect.getStrength() <= 0) {
                log.trace("Effect has been resisted");
                return;
            }
        }

        effects.add(effect);
    }
    public void addEffects(List<EncounterEffect> effects) {
        effects.forEach(this::addEffect);
    }
    public void damage(int damage) {
        log.trace("Dealing {} damage to entity '{}'", damage, this);
        health -= damage;
    }
    public void applyEffect(EncounterEffect effect) {
        // TODO logic for applying the effect goes here
        if (!effect.isApplicableAtStartTurn())
            return;

        for (var e : effects.stream().filter(EncounterEffect::isApplicableAtStartTurn).toList()) {
            log.trace("Applying effect '{}' for entity '{}'", e, this);
            switch (e.getType()) {
                case POISON, FIRE, BLEED -> damage(e.getStrength());
                case REGENERATION -> health += e.getStrength();
            }
        }
    }
    public void decreaseEffectDuration(EncounterEffect effect) {
        effect.decreaseDuration();

        if (effect.isExpired()) {
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

    public int getBonusInitiative() {
        return effects.stream()
                .filter(e -> e.getType().equals(EffectDTO.EffectType.BONUS_INITIATIVE))
                .mapToInt(EncounterEffect::getStrength)
                .sum();
    }
    public int getInitiative() {
        return initiative + getBonusInitiative();
    }

    public int getBonusDefence() {
        return effects.stream()
                .filter(e -> e.getType().equals(EffectDTO.EffectType.BONUS_DEFENCE))
                .mapToInt(EncounterEffect::getStrength)
                .sum();
    }
    public int getDefence() {
        return defence + getBonusDefence();
    }

    public int getBonusHealth() {
        return effects.stream()
                .filter(e -> e.getType().equals(EffectDTO.EffectType.BONUS_HEALTH))
                .mapToInt(EncounterEffect::getStrength)
                .sum();
    }
    public int getHealth() {
        return health + getBonusHealth();
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
