package cz.trailsthroughshadows.api.table.schematic.obstacle.model;

import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexDTO;
import cz.trailsthroughshadows.api.images.ImageLoader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;

@Data
@EqualsAndHashCode(callSuper = true)
public class Obstacle extends ObstacleDTO {

    private HexDTO hex;

    private String url;

    public String getUrl() {
        return ImageLoader.getPath(getTag());
    }

    public static Obstacle fromDTO(ObstacleDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Obstacle.class);
    }

    public static Obstacle fromDTO(ObstacleDTO dto, HexDTO hex) {
        Obstacle obstacle = fromDTO(dto);
        obstacle.setHex(hex);

        return obstacle;
    }

    public static ObstacleDTO toDTO(Obstacle obstacle) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(obstacle, ObstacleDTO.class);
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
