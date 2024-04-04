package cz.trailsthroughshadows.api.table.effect.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EffectEnum {

    private String name;
    private String displayName;
    private String description;
    private boolean hasDuration;
    private boolean hasStrength;
    private String url;

    public String getUrl() {
        return "/effects/" + name;
    }

    public static EffectEnum fromDTO(EffectDTO dto) {
        return new EffectEnum(dto.getType().name(), dto.getType().displayName, dto.getDescription(),
                dto.getType().hasDuration, dto.getType().hasStrength, null);
    }

    public static EffectEnum fromEnum(EffectDTO.EffectType type) {
        return new EffectEnum(type.name(), type.displayName, null, type.hasDuration, type.hasStrength, null);
    }

}
