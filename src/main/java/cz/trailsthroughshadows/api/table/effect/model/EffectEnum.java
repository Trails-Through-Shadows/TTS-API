package cz.trailsthroughshadows.api.table.effect.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EffectEnum {

    private String title;
    private String displayTitle;
    private String description;
    private boolean hasDuration;
    private boolean hasStrength;
    @JsonProperty("isResistance")
    private boolean isResistance;
    private String url;

    public String getUrl() {
        return "/effects/" + title;
    }

    public static EffectEnum fromDTO(EffectDTO dto) {
        return new EffectEnum(dto.getType().name(), dto.getType().displayName, dto.getDescription(),
                dto.getType().hasDuration, dto.getType().hasStrength, dto.getType().isResistance, null);
    }

    public static EffectEnum fromEnum(EffectDTO.EffectType type) {
        return new EffectEnum(type.name(), type.displayName, null, type.hasDuration, type.hasStrength,
                type.isResistance, null);
    }

}
