package cz.trailsthroughshadows.api.table.schematic.location.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import cz.trailsthroughshadows.api.util.ImageLoader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Location extends LocationDTO {

    private String url;

    public String getUrl() {
        return ImageLoader.getPath(getTag());
    }


    // TODO: Map locations only by specific campaign frrom database @rcMarty
    public static Location fromDTO(LocationDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Location.class);
    }

    @Override
    public List<Part> getMappedParts() {
        if (parts == null) {
            return new ArrayList<>();
        }

        return parts.stream()
                .map(locationPart -> Part.fromDTO(
                        locationPart.getPart(),
                        locationPart.getRotation(),
                        enemies.stream()
                                .filter(hexEnemy -> hexEnemy.getKey().getIdPart() == locationPart.getPart().getId())
                                .toList(),
                        obstacles.stream()
                                .filter(hexObstacle -> hexObstacle.getKey().getIdPart() == locationPart.getPart().getId())
                                .toList()
                )).toList();
    }

    public enum Type {
        CITY, DUNGEON, MARKET, QUEST
    }
}
