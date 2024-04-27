package cz.trailsthroughshadows.api.table.schematic.hex.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexObstacleDTO;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;

@EqualsAndHashCode(callSuper = true)
@Data
public class HexObstacle extends HexObstacleDTO {

    @JsonProperty("obstacle")
    private Object remappedObstacle;

    public static HexObstacle fromDTO(HexObstacleDTO dto) {

        if (dto == null) {
            return null;
        }

        ModelMapper modelMapper = new ModelMapper();
        HexObstacle hexObstacle = modelMapper.map(dto, HexObstacle.class);

        if (Hibernate.isInitialized(dto.getObstacle())) {
            hexObstacle.remappedObstacle = Obstacle.fromDTO(dto.getObstacle());
        } else {
            hexObstacle.remappedObstacle = dto.getObstacle();
        }

        return hexObstacle;
    }

}
