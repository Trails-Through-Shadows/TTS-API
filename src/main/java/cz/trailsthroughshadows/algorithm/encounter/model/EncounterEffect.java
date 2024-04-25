package cz.trailsthroughshadows.algorithm.encounter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.trailsthroughshadows.api.table.effect.model.Effect;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class EncounterEffect {

    private EffectDTO.EffectType type;
    private Integer strength;
    private Integer duration;
    private String description;

    public static EncounterEffect fromEffect(Effect effect) {
        EncounterEffect encounterEffect = new EncounterEffect();
        encounterEffect.setType(effect.getType());
        encounterEffect.setStrength(effect.getStrength());
        encounterEffect.setDescription(effect.getDescription());
        encounterEffect.setDuration(effect.getDuration());

        return encounterEffect;
    }

    public Effect toEffect() {
        Effect effect = new Effect();
        effect.setTarget(EffectDTO.EffectTarget.SELF);
        effect.setType(type);
        effect.setStrength(strength);
        effect.setDescription(description);
        effect.setDuration(duration);

        return effect;
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
            case STUN -> EffectDTO.EffectType.STUN_RESISTANCE;
            case ENFEEBLE -> EffectDTO.EffectType.ENFEEBLE_RESISTANCE;
            case WEAKNESS -> EffectDTO.EffectType.WEAKNESS_RESISTANCE;
            default -> null;
        };
    }

    @JsonIgnore
    public boolean isInfinite() {
        return getDuration() == -1;
    }

    @JsonIgnore
    public boolean isExpired() {
        return getDuration() == 0;
    }

    @JsonIgnore
    public boolean isApplicableAtStartTurn() {
        // all in: poison, fire, bleed, regeneration
        return switch (getType()) {
            case POISON, FIRE, BLEED, REGENERATION -> true;
            default -> false;
        };
    }

    @JsonIgnore
    public boolean isInstant() {
        return getDuration() == null;
    }

    @JsonIgnore
    public boolean hasResistance() {
        return getResistanceType(this) != null;
    }

    @JsonIgnore
    public void decreaseDuration() {
        if (isInfinite())
            return;

        log.trace("Decreasing effect duration '{}'", this);
        duration = Math.max(0, duration - 1);
    }

    @JsonIgnore
    public void decreaseStrength(int resistance) {
        strength = Math.max(0, strength - resistance);
    }

    @Override
    public String toString() {
        return "%s %d for %d rounds".formatted(type, strength, duration);
    }
}
