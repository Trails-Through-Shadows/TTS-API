package cz.trailsthroughshadows.api.table.background.clazz.model;

import cz.trailsthroughshadows.api.images.ImageLoader;
import org.modelmapper.ModelMapper;

public class Clazz extends ClazzDTO {

    public static Clazz fromDTO(ClazzDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Clazz.class);
    }

    public String getUrl() {
        return ImageLoader.getPath(getTag());
    }
}
