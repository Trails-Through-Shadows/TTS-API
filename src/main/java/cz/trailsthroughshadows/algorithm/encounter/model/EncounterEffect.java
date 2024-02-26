package cz.trailsthroughshadows.algorithm.encounter.model;

import cz.trailsthroughshadows.api.table.effect.model.Effect;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import lombok.Data;

@Data
public class EncounterEffect {

    private Integer id;
    private EffectDTO.EffectType type;
    private Integer strength;
    private Integer duration;
    private String description;

    public static EncounterEffect fromEffect(Effect effect) {
        EncounterEffect encounterEffect = new EncounterEffect();
        encounterEffect.setId(effect.getId());
        encounterEffect.setType(effect.getType());
        encounterEffect.setStrength(effect.getStrength());
        encounterEffect.setDescription(effect.getDescription());
        encounterEffect.setDuration(effect.getDuration());

        return encounterEffect;
    }

    public static EncounterEffect fromEffect(EffectDTO effect) {
        return fromEffect(Effect.fromDTO(effect));
    }

    public static EffectDTO.EffectType getResistanceType(EncounterEffect effect) {
        return switch (effect.getType()) {
            case PUSH, PULL -> EffectDTO.EffectType.FORCED_MOVEMENT_RESISTANCE;
            case POISON -> EffectDTO.EffectType.POISON_RESISTANCE;
            case FIRE -> EffectDTO.EffectType.FIRE_RESISTANCE;
            case BLEED -> EffectDTO.EffectType.BLEED_RESISTANCE;
            case DISARM -> EffectDTO.EffectType.DISARM_RESISTANCE;
            case ROOT -> EffectDTO.EffectType.ROOT_RESISTANCE;
            case STUN -> EffectDTO.EffectType.STUN_RESISTANCE;
            case CONFUSION -> EffectDTO.EffectType.CONFUSION_RESISTANCE;
            case ENFEEBLE -> EffectDTO.EffectType.ENFEEBLE_RESISTANCE;
            case SLOW -> EffectDTO.EffectType.SLOW_RESISTANCE;
            case CONSTRAIN -> EffectDTO.EffectType.CONSTRAIN_RESISTANCE;
            default -> null;
        };
    }

    public boolean isInfinite() {
        return getDuration() == -1;
    }
    public boolean isExpired() {
        return getDuration() == 0;
    }

    public boolean isApplicableAtStartTurn() {
        // all in: poison, fire, bleed, regeneration
        return switch (getType()) {
            case POISON, FIRE, BLEED, REGENERATION -> true;
            default -> false;
        };
    }

    public boolean hasResistance() {
        return getResistanceType(this) != null;
    }

    @Override
    public String toString() {
        return "%s %d for %d rounds".formatted(type, strength, duration);
    }
}
