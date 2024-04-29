package cz.trailsthroughshadows.api.table.schematic.location.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.trailsthroughshadows.api.images.ImageLoader;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.schematic.hex.model.HexEnemy;
import cz.trailsthroughshadows.api.table.schematic.hex.model.HexObstacle;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationPartDTO;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import cz.trailsthroughshadows.api.table.schematic.part.model.PartDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class Location extends LocationDTO {

    @JsonProperty("enemies")
    List<Object> mappedHexEnemies;

    //private List<Story> stories;
    @JsonProperty("obstacles")
    List<Object> mappedHexObstacles;
    private String url;

    // TODO: Map locations only by specific campaign frrom database @rcMarty
    public static Location fromDTO(LocationDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        Location loc = modelMapper.map(dto, Location.class);
        log.debug("enemies are initialized: {}", Hibernate.isInitialized(dto.getEnemies()));

        if (Hibernate.isInitialized(dto.getEnemies())) {
            loc.mappedHexEnemies = dto.getEnemies().stream().map(HexEnemy::fromDTO).map(e -> (Object) e).toList();
            loc.getMappedHexEnemies().forEach(hexenemy -> {
                if (((HexEnemy) hexenemy).getRemappedEnemy() instanceof Enemy) {
                    dto.getMappedParts().stream()
                            .map(part -> Part.getMappedEnemiesFromDTO(part, dto.getEnemies()))
                            .flatMap(List::stream)
                            .filter(enemy -> Objects.equals((((Enemy) ((HexEnemy) hexenemy).getRemappedEnemy())).getId(), enemy.getId()))
                            .filter(enemy -> Objects.equals((((HexEnemy) hexenemy).getKey().getIdHex()), enemy.getStartingHex().getKey().getId()))
                            .findFirst()
                            .ifPresent(((HexEnemy) hexenemy)::setRemappedEnemy);
                }
            });
        } else {
            loc.mappedHexEnemies = dto.getEnemies().stream().map(e -> (Object) e).toList();
        }

        log.debug("obstacles are initialized: {}", Hibernate.isInitialized(dto.getObstacles()));

        if (Hibernate.isInitialized(dto.getObstacles())) {
            loc.mappedHexObstacles = dto.getObstacles().stream().map(HexObstacle::fromDTO).map(e -> (Object) e).toList();
            loc.getMappedHexObstacles().forEach(hexobstacle -> {
                if (((HexObstacle) hexobstacle).getRemappedObstacle() instanceof Obstacle) {
                    dto.getMappedParts().stream()
                            .map(part -> Part.getMappedObstaclesFromDTO(part, dto.getObstacles()))
                            .flatMap(List::stream)
                            .filter(obstacle -> Objects.equals((((Obstacle) ((HexObstacle) hexobstacle).getRemappedObstacle())).getId(), obstacle.getId()))
                            .filter(obstacle -> Objects.equals((((HexObstacle) hexobstacle).getKey().getIdHex()), obstacle.getHex().getKey().getId()))
                            .findFirst()
                            .ifPresent(((HexObstacle) hexobstacle)::setRemappedObstacle);
                }
            });
        } else {
            loc.mappedHexObstacles = dto.getObstacles().stream().map(e -> (Object) e).toList();
        }

        return loc;
    }

    public String getUrl() {
        if (url == null)
            url = ImageLoader.getPath(this.getTag());
        return ImageLoader.getPath(this.getTag());
    }


//    public List<Story> findStories(CampaignRepo campaignRepo) {
//        if (stories == null) {
//            stories = campaignRepo.findAllStoriesByCampaignId(this.getId());
//        }
//        return stories;
//    }

    @JsonIgnore
    public Part getStartPart() {
        ModelMapper modelMapper = new ModelMapper();
        List<Part> parts = getParts().stream()
                .map(LocationPartDTO::getPart)
                .map(partDTO -> modelMapper.map(partDTO, Part.class))
                .toList();

        PartDTO tmp = parts.stream()
                .filter(part -> Objects.equals(
                        part.getId(),
                        this.getStartHexes()
                                .stream()
                                .findFirst()
                                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "No start part found in location {}", getId()))
                                .getIdPart())) // hoping there will be only one starting part
                .findFirst()
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "No start part found in location {}", getId()));

        return Part.fromDTO(tmp);
    }

//    /**
//     * Get all mapped Hexes from LocationStartDTO
//     *
//     * @return list of mapped hexes
//     */
//    @JsonIgnore
//    public List<HexDTO> getStartingHexes() {
//        return startHexes
//    }

    @JsonIgnore
    public List<Part> getMappedParts() {
        if (parts == null) {
            return new ArrayList<>();
        }

        return parts.stream()
                .map(locationPart -> Part.fromDTO(
                        locationPart.getPart(),
                        locationPart.getRotation(),
                        this.getEnemies(),
                        this.getObstacles()))
                .toList();
    }

    public enum Type {
        CITY, DUNGEON, MARKET, QUEST
    }
}
