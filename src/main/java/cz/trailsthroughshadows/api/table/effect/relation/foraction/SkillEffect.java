package cz.trailsthroughshadows.api.table.effect.relation.foraction;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.trailsthroughshadows.api.table.effect.model.Effect;
import lombok.Getter;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;


@Getter
public class SkillEffect extends SkillEffectDTO {

    @JsonProperty("effect")
    private Object mappedEffect;

    public static SkillEffect fromDTO(SkillEffectDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        SkillEffect effect = modelMapper.map(dto, SkillEffect.class);
        if (Hibernate.isInitialized(dto.getEffect())) {
            effect.mappedEffect = Effect.fromDTO(dto.getEffect());
        } else {
            effect.mappedEffect = dto.getEffect();
        }
        return effect;
    }
}
