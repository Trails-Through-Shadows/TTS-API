package cz.trailsthroughshadows.api.table.background.clazz.model;

import org.modelmapper.ModelMapper;

public class Clazz extends ClazzDTO {

    public static Clazz fromDTO(ClazzDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Clazz.class);
    }
}
