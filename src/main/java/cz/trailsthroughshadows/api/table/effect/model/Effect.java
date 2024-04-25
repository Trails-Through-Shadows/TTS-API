package cz.trailsthroughshadows.api.table.effect.model;

import cz.trailsthroughshadows.api.images.ImageLoader;
import lombok.Getter;
import org.modelmapper.ModelMapper;

@Getter
public class Effect extends EffectDTO {

    public static Effect fromDTO(EffectDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Effect.class);
    }

    public String getUrl() {
        return ImageLoader.getPath("f-" + getType().toString().toLowerCase());
    }
}
