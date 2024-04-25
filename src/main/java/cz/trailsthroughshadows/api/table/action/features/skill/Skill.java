package cz.trailsthroughshadows.api.table.action.features.skill;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.trailsthroughshadows.api.table.effect.relation.foraction.SkillEffect;
import lombok.Getter;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;

import java.util.List;

@Getter
public class Skill extends SkillDTO {

    @JsonProperty("effects")
    List<Object> remappedEffects;

    public static Skill fromDTO(SkillDTO dto) {

        if (dto == null) {
            return null;
        }

        ModelMapper modelMapper = new ModelMapper();
        Skill skill = modelMapper.map(dto, Skill.class);
        if (Hibernate.isInitialized(dto.getEffects())) {
            skill.remappedEffects = dto.getEffects().stream().map(SkillEffect::fromDTO).map(e -> (Object) e).toList();
        } else {
            skill.remappedEffects = dto.getEffects().stream().map(e -> (Object) e).toList();
        }
        return skill;
    }
}
