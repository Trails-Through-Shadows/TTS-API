package cz.trailsthroughshadows.api.table.schematic.hex.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Hex extends HexDTO {

    public static Hex fromDTO(HexDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Hex.class);
    }
}
