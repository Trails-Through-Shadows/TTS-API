package cz.trailsthroughshadows.api.table.effect.model;

import org.modelmapper.ModelMapper;

public class Effect extends EffectDTO {

    public static Effect fromDTO(EffectDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Effect.class);
    }
}
