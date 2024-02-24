package cz.trailsthroughshadows.api.table.playerdata.adventure.model;
import lombok.Data;
import org.modelmapper.ModelMapper;

@Data
public class Adventure extends AdventureDTO {


    public static Adventure fromDTO(AdventureDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Adventure.class);
    }
}
