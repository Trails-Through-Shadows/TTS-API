package cz.trailsthroughshadows.api.table.schematic.part.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.algorithm.location.Navigation;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.enemy.model.dto.HexEnemyDTO;
import cz.trailsthroughshadows.api.table.schematic.hex.model.Hex;
import cz.trailsthroughshadows.api.table.schematic.hex.model.HexDTO;
import cz.trailsthroughshadows.api.table.schematic.hex.model.HexObstacleDTO;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Part extends PartDTO implements Validable {

    private Integer rotation;
    private List<Enemy> enemies;
    private List<Obstacle> obstacles;
    // TODO: Add doors to part?

    public static Part fromDTO(PartDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Part.class);
    }

    public static Part fromDTO(PartDTO dto, LocationDTO location, int rotation, List<HexEnemyDTO> enemies, List<HexObstacleDTO> obstacles) {
        Part part = fromDTO(dto);
        part.setRotation(rotation);
        part.setEnemies(enemies.stream().map(e -> Enemy.fromDTO(e.getEnemy(), part.getHex(e.getKey().getIdHex()).orElse(null))).toList());
        part.setObstacles(obstacles.stream().map(o -> Obstacle.fromDTO(o.getObstacle(), part.getHex(o.getKey().getIdHex()).orElse(null))).toList());

        return part;
    }

    public static PartDTO toDTO(Part part) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(part, PartDTO.class);
    }

    public Optional<Hex> getHex(int id) {
        return getHexes().stream().filter(h -> h.getKey().getId() == id).findFirst().map(Hex::fromDTO);
    }

    public Optional<Enemy> getEnemy(Hex hex) {
        return enemies.stream().filter(e -> e.getHex().equals(hex)).findFirst();
    }

    public Optional<Obstacle> getObstacle(Hex hex) {
        return obstacles.stream().filter(o -> o.getHex().equals(hex)).findFirst();
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        int minHexes = 5;
        int maxHexes = 50;
        int maxHexesWide = 8;

        // min 5 hexes
        if (getHexes().size() < minHexes) {
            errors.add("Part must have at least %d hexes!".formatted(minHexes));
        }

        // max 50 hexes
        if (getHexes().size() > maxHexes) {
            errors.add("Part must have at most 50 hexes!");
        }

        // every hex has to have correct coordinates
        for (HexDTO hex : getHexes()) {
            errors.addAll(Hex.fromDTO(hex).validate());
        }
        if (!errors.isEmpty())
            return errors;

        // max 8 hexes wide
        List<Integer> coordinates = new ArrayList<>();

        IntSummaryStatistics qStats = getHexes().stream().mapToInt(HexDTO::getQ).summaryStatistics();
        IntSummaryStatistics rStats = getHexes().stream().mapToInt(HexDTO::getR).summaryStatistics();
        IntSummaryStatistics sStats = getHexes().stream().mapToInt(HexDTO::getS).summaryStatistics();

        int diffQ = qStats.getMax() - qStats.getMin() - 1;
        int diffR = rStats.getMax() - rStats.getMin() - 1;
        int diffS = sStats.getMax() - sStats.getMin() - 1;

        if (diffQ > maxHexesWide || diffR > maxHexesWide || diffS > maxHexesWide) {
            errors.add("Part must not be wider than %d hexes!".formatted(maxHexesWide));
        }

        // no hexes can be on the same position
        int duplicates = 0;
        for (HexDTO hex1 : getHexes()) {
            for (HexDTO hex2 : getHexes()) {
                if (hex1 == hex2)
                    continue;

                if (hex1.getQ() == hex2.getQ() && hex1.getR() == hex2.getR() && hex1.getS() == hex2.getS()) {
                    duplicates++;
                    break;
                }
            }
        }
        if (duplicates > 0)
            errors.add("Part must not have duplicate hexes!");

        // must include center hex
        Optional<HexDTO> centerHex = getHexes().stream().filter(hex -> hex.getQ() == 0 && hex.getR() == 0 && hex.getS() == 0).findFirst();
        if (centerHex.isEmpty()) {
            errors.add("Part must include a center hex!");
            return errors;
        }

        // all hexes must be connected
        Navigation navigation = new Navigation(this);

        for (HexDTO hex : getHexes()) {
            if (hex == centerHex.get())
                continue;

            if (navigation.getPath(centerHex.get(), hex) == null) {
                errors.add("All hexes must be connected!");
                break;
            }
        }

        return errors;
    }

    @Override
    public String getIdentifier() {
        return getTag();
    }
}
