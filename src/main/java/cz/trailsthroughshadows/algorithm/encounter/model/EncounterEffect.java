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

        return encounterEffect;
    }
}
