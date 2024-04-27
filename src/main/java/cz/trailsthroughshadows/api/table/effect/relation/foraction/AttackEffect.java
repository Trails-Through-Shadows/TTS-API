package cz.trailsthroughshadows.api.table.effect.relation.foraction;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.trailsthroughshadows.api.table.effect.model.Effect;
import lombok.Getter;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;

@Getter
public class AttackEffect extends AttackEffectDTO {

    @JsonProperty("effect")
    private Object mappedEffect;

    public static AttackEffect fromDTO(AttackEffectDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        AttackEffect effect = modelMapper.map(dto, AttackEffect.class);

        if (Hibernate.isInitialized(dto.getEffect())) {
            effect.mappedEffect = Effect.fromDTO(dto.getEffect());
        } else {
            effect.mappedEffect = dto.getEffect();
        }

        return effect;
    }
}
