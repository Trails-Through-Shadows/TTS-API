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

    @Override
    public String toString() {
        return "%s %d for %d rounds".formatted(type, strength, duration);
    }
}
