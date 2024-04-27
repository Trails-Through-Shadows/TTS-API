package cz.trailsthroughshadows.api.table.schematic.hex.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexObstacleDTO;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import lombok.Getter;
import org.modelmapper.ModelMapper;

@Getter
public class HexObstacle extends HexObstacleDTO {

    @JsonProperty("obstacle")
    private Obstacle remappedobstacle;

    public static HexObstacle fromDTO(HexObstacleDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        HexObstacle hexObstacle = modelMapper.map(dto, HexObstacle.class);
        hexObstacle.remappedobstacle = modelMapper.map(dto.getObstacle(), Obstacle.class);
        return hexObstacle;
    }

}
