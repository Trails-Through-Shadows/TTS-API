package cz.trailsthroughshadows.api.table.effect.relation.foraction;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.trailsthroughshadows.api.table.effect.model.Effect;
import lombok.Getter;
import org.modelmapper.ModelMapper;

@Getter
public class AttackEffect extends AttackEffectDTO {

    @JsonProperty("effect")
    private Effect mappedEffect;

    public static AttackEffect fromDTO(AttackEffectDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        AttackEffect effect = modelMapper.map(dto, AttackEffect.class);
        if (dto.getEffect() != null) {
            effect.mappedEffect = Effect.fromDTO(dto.getEffect());
        }
        return effect;
    }
}
