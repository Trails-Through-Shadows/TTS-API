package cz.trailsthroughshadows.api.table.schematic.obstacle.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.api.table.schematic.hex.model.HexDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Obstacle extends ObstacleDTO {

    private HexDTO hex;
    
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
}
