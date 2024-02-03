package cz.trailsthroughshadows.api.table.schematic.location.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Location extends LocationDTO {

    private List<Hex> startingHexes;

    public enum Type {
        CITY, DUNGEON, MARKET, QUEST
    }

    public static Location fromDTO(LocationDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Location.class);
    }
}
