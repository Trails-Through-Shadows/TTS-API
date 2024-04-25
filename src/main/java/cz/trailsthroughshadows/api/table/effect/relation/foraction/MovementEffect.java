package cz.trailsthroughshadows.api.table.effect.relation.foraction;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.trailsthroughshadows.api.table.effect.model.Effect;
import lombok.Getter;
import org.modelmapper.ModelMapper;

@Getter
public class MovementEffect extends MovementEffectDTO {

    @JsonProperty("effect")
    private Effect mappedEffect;

    public static MovementEffect fromDTO(MovementEffectDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        MovementEffect effect = modelMapper.map(dto, MovementEffect.class);
        if (dto.getEffect() != null) {
            effect.mappedEffect = Effect.fromDTO(dto.getEffect());
        }
        return effect;
    }
}
