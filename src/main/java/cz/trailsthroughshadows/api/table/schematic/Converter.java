package cz.trailsthroughshadows.api.table.schematic;

import org.modelmapper.ModelMapper;

public interface Converter<From, To> {

    static <From, To> To aaa(From source) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(source, (Class<To>) source.getClass());
    }

    static <From, To> From toDTO(To source, Class<From> targetType) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(source, targetType);
    }
}

