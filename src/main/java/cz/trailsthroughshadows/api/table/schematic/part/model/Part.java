package cz.trailsthroughshadows.api.table.schematic.part.model;

import cz.trailsthroughshadows.api.images.ImageLoader;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.schematic.hex.model.Hex;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexEnemyDTO;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexObstacleDTO;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDoorDTO;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
public class Part extends PartDTO {

    private Integer rotation;

    private List<Enemy> enemies;

    private List<Obstacle> obstacles;

    private List<LocationDoorDTO> doors;

    private List<Hex> startingHexes;

    private Boolean unlocked = false;

    private String url;

    public String getUrl() {
        if (url == null)
            url = ImageLoader.getPath(getTag());
        return ImageLoader.getPath(getTag());
    }

    public void setStartingHexes(List<Hex> startingHexes) {
        // TODO @kkobari validation
        this.startingHexes = startingHexes;
    }

    private void setFilterEnemies(List<HexEnemyDTO> enemies) {
        this.setEnemies(enemies.stream()
                .filter(e -> e.getKey().getIdPart() == this.getId())
                .map(e -> Enemy.fromDTO(e.getEnemy(), this.getHex(e.getKey().getIdHex())
                        .orElse(null)))
                .toList());
    }

    private void setFilterObstacles(List<HexObstacleDTO> obstacles) {
        this.setObstacles(obstacles.stream()
                .filter(o -> Objects.equals(o.getKey().getIdPart(), this.getId()))
                .map(o -> Obstacle.fromDTO(o.getObstacle(), this.getHex(o.getKey().getIdHex())
                        .orElse(null)))
                .toList());
    }

    private void setFilterDoors(List<LocationDoorDTO> doors) {
        this.setDoors(doors.stream()
                .filter(e -> e.getKey().getIdPartFrom() == this.getId())
                .toList());
    }

    public static Part fromDTO(PartDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Part.class);
    }

    /**
     * Create a Part from a DTO and add the enemies, obstacles and doors to it based
     * on Part ID
     * 
     * @implNote This constructor filters enemies, obstacles, and doors by the ID of
     *           the Part
     * @param dto       dto object of Part
     * @param rotation  rotation of the part (must be filtered by the caller)
     * @param enemies   list of all DTO enemies from that Location (is filtered by
     *                  id in constructor)
     * @param obstacles list of all DTO obstacles from that Location (is filtered by
     *                  id in constructor)
     * @param doors     list of all DTO doors from that Location (is filtered by id
     *                  in constructor)
     * @return Part object
     */
    public static Part fromDTO(PartDTO dto, Integer rotation, List<HexEnemyDTO> enemies, List<HexObstacleDTO> obstacles,
            List<LocationDoorDTO> doors) {
        Part part = fromDTO(dto);
        part.setRotation(rotation);
        part.setFilterEnemies(enemies);
        part.setFilterObstacles(obstacles);
        part.setFilterDoors(doors);
        return part;
    }

    /**
     * Create a Part from a DTO and add the enemies, obstacles and doors to it based
     * on Part ID
     * 
     * @implNote This constructor filters enemies, obstacles, and doors by the ID of
     *           the Part
     * @param dto       dto object of Part
     * @param rotation  rotation of the part (must be filtered by the caller)
     * @param enemies   list of all DTO enemies from that Location (is filtered by
     *                  id in constructor)
     * @param obstacles list of all DTO obstacles from that Location (is filtered by
     *                  id in constructor)
     * @return Part object
     */
    public static Part fromDTO(PartDTO dto, Integer rotation, List<HexEnemyDTO> enemies,
            List<HexObstacleDTO> obstacles) {
        Part part = fromDTO(dto);
        part.setRotation(rotation);
        part.setFilterEnemies(enemies);
        part.setFilterObstacles(obstacles);
        return part;
    }

    /**
     * Create a Part from a DTO and add the enemies, obstacles and doors to it based
     * on Part ID
     * 
     * @implNote This constructor filters enemies, obstacles, and doors by the ID of
     *           the Part
     * @param dto       dto object of Part
     * @param obstacles list of all DTO obstacles from that Location (is filtered by
     *                  id in constructor)
     * @param doors     list of all DTO doors from that Location (is filtered by id
     *                  in constructor)
     * @return Part object
     */
    public static Part fromDTO(PartDTO dto, List<HexObstacleDTO> obstacles, List<LocationDoorDTO> doors) {
        Part part = fromDTO(dto);
        part.setFilterObstacles(obstacles);
        part.setFilterDoors(doors);
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

    public void unlock() {
        unlocked = true;
    }
}
