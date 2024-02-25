package cz.trailsthroughshadows.api.table.schematic.part.model;

import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.schematic.hex.model.Hex;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexEnemyDTO;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexObstacleDTO;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import cz.trailsthroughshadows.api.util.ImageLoader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
public class Part extends PartDTO {

    private Integer rotation;

    private List<Enemy> enemies;

    private List<Obstacle> obstacles;
    // TODO: Add doors to part?

    private boolean unlocked;

    private String url;

    public String getUrl() {
        return ImageLoader.getPath(getTag());
    }


    public static Part fromDTO(PartDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Part.class);
    }

    public static Part fromDTO(PartDTO dto, int rotation, List<HexEnemyDTO> enemies, List<HexObstacleDTO> obstacles) {
        Part part = fromDTO(dto);
        part.setRotation(rotation);
        part.setEnemies(enemies.stream().map(e -> Enemy.fromDTO(e.getEnemy(), part.getHex(e.getKey().getIdHex()).orElse(null))).toList());
        part.setObstacles(obstacles.stream().map(o -> Obstacle.fromDTO(o.getObstacle(), part.getHex(o.getKey().getIdHex()).orElse(null))).toList());
        return part;
    }

    public Optional<Hex> getHex(int id) {
        return hexes.stream().filter(h -> h.getKey().getId() == id).findFirst().map(Hex::fromDTO);
    }

    public Optional<Enemy> getEnemy(Hex hex) {
        return enemies.stream().filter(e -> e.getHex().equals(hex)).findFirst();
    }

    public Optional<Obstacle> getObstacle(Hex hex) {
        return obstacles.stream().filter(o -> o.getHex().equals(hex)).findFirst();
    }
}
