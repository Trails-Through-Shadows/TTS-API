package cz.trailsthroughshadows.api.table.action.features.movement;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.trailsthroughshadows.api.table.effect.relation.foraction.MovementEffect;
import lombok.Getter;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;

import java.util.List;

@Getter
public class Movement extends MovementDTO {

    @JsonProperty("effects")
    private List<Object> remappedEffects;

    public static Movement fromDTO(MovementDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        Movement movement = modelMapper.map(dto, Movement.class);

        if (Hibernate.isInitialized(dto.getEffects())) {
            movement.remappedEffects = dto.getEffects().stream().map(MovementEffect::fromDTO).map(e -> (Object) e).toList();
        } else {
            movement.remappedEffects = dto.getEffects().stream().map(e -> (Object) e).toList();
        }


        return movement;
    }


}
