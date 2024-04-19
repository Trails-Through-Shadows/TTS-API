package cz.trailsthroughshadows.api.table.background.race.model;

import cz.trailsthroughshadows.api.images.ImageLoader;
import org.modelmapper.ModelMapper;

public class Race extends RaceDTO {

    public static Race fromDTO(RaceDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Race.class);
    }

    public String getUrl() {
        return ImageLoader.getPath(getTag());
    }
}
