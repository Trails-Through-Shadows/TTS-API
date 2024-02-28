package cz.trailsthroughshadows.algorithm.encounter.model;

import cz.trailsthroughshadows.api.table.action.features.summon.model.Summon;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
        log.debug("Adding effect '{}' to entity '{}'", effect, this);

        List<EncounterEffect> resistances = effects.stream()
                .filter(e -> e.getType().equals(EncounterEffect.getResistanceType(effect)))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (!resistances.isEmpty()) {
            log.trace("Entity has resistance: {}", resistances);
            int resistance = 0;

            for (var r : resistances) {
                if (r.getStrength() == -1) {
                    log.trace("Entity has immunity for this type of effect");
                    effect.setStrength(0);
                    break;
                }

                effect.decreaseStrength(r.getStrength());
            }

            effect.decreaseStrength(resistance);

            if (effect.getStrength() <= 0) {
                log.trace("Effect has been resisted");
                return;
            }

            log.trace("Effect strength has been reduced to '{}'", effect);
        }

        effects.add(effect);
    }
    public void addEffects(List<EncounterEffect> effects) {
        effects.forEach(this::addEffect);
    }
    public void applyEffect(EncounterEffect effect) {
        if (!effect.isApplicableAtStartTurn())
            return;

        for (var e : effects.stream().filter(EncounterEffect::isApplicableAtStartTurn).toList()) {
            log.trace("Applying effect '{}' for entity '{}'", e, this);
            switch (e.getType()) {
                case POISON, FIRE, BLEED -> damage(e.getStrength(), DamageSource.EFFECT);
                case REGENERATION -> health += e.getStrength();
            }
        }
    }
    public void decreaseEffectDuration(EncounterEffect effect) {
        effect.decreaseDuration();
    }

    public void damage(int damage, DamageSource source) {
        if (damage <= 0)
            return;

        log.debug("Dealing {} damage from {} to entity '{}'", damage, source, this);
        if (source == DamageSource.ATTACK && getDefence() > 0) {
            damage = Math.max(0, damage - getDefence());
            log.trace("Damage reduced by defence to {}", damage);
        }
        health =  Math.max(0, health - damage);
        log.trace("Entity health is now {}", health);
    }

    public void startTurn() {
        log.debug("Starting turn for entity '{}'", entity);
        effects.forEach(this::applyEffect);

        // todo add summons
    }
    public void endTurn() {
        log.debug("Ending turn for entity '{}'", entity);
        effects.forEach(this::decreaseEffectDuration);

        for (var e : effects.stream().filter(EncounterEffect::isExpired).toList()) {
            log.trace("Removing expired effect '{}'", e);
            effects.remove(e);
        }

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

    public EntityStatusUpdate getStatusUpdate() {
        return new EntityStatusUpdate(getId(), getHealth(), getEffects(), getHealth() != 0 ? EntityStatusUpdate.Status.ALIVE : EntityStatusUpdate.Status.DEAD);
    }

    public enum EntityType {
        CHARACTER,
        ENEMY,
        SUMMON,
        OBSTACLE,
    }

    public enum DamageSource {
        ATTACK,
        EFFECT,
    }
}
