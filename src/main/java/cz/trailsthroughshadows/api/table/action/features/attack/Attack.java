package cz.trailsthroughshadows.api.table.action.features.attack;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.trailsthroughshadows.api.table.effect.relation.foraction.AttackEffect;
import lombok.Getter;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;

import java.util.List;

@Getter
public class Attack extends AttackDTO {

    @JsonProperty("effects")
    private List<Object> remappedEffects;

    public static Attack fromDTO(AttackDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        Attack attack = modelMapper.map(dto, Attack.class);

        if (Hibernate.isInitialized(dto.getEffects())) {
            attack.remappedEffects = dto.getEffects().stream().map(AttackEffect::fromDTO).map(e -> (Object) e).toList();
        } else {
            attack.remappedEffects = dto.getEffects().stream().map(e -> (Object) e).toList();
        }
        return attack;
    }
}
