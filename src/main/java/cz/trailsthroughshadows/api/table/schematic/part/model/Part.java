package cz.trailsthroughshadows.api.table.schematic.part.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.enemy.model.dto.HexEnemyDTO;
import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import cz.trailsthroughshadows.api.table.schematic.obstacle.HexObstacle;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Part extends PartDTO {

    private Integer rotation;
    private List<Enemy> enemies;
    private List<Obstacle> obstacles;
    // TODO: Add doors to part?

    public static Part fromDTO(PartDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Part.class);
    }

    public static Part fromDTO(PartDTO dto, LocationDTO location, int rotation, List<HexEnemyDTO> enemies, List<HexObstacle> obstacles) {
        Part part = fromDTO(dto);
        part.setRotation(rotation);
        part.setEnemies(enemies.stream().map(e -> Enemy.fromDTO(e.getEnemy(), part.getHex(e.getKey().getIdHex()).orElse(null))).toList());
        part.setObstacles(obstacles.stream().map(o -> Obstacle.fromDTO(o.getObstacle(), part.getHex(o.getKey().getIdHex()).orElse(null))).toList());

        return part;
    }

    private Optional<Hex> getHex(int id) {
        return getHexes().stream().filter(h -> h.getKey().getId() == id).findFirst();
    }

    private Optional<Enemy> getEnemy(Hex hex) {
        return enemies.stream().filter(e -> e.getHex().equals(hex)).findFirst();
    }

    public Optional<Obstacle> getObstacle(Hex hex) {
        return obstacles.stream().filter(o -> o.getHex().equals(hex)).findFirst();
    }
}
