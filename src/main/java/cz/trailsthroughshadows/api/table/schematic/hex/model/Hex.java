package cz.trailsthroughshadows.api.table.schematic.hex.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Hex extends HexDTO implements Validable {

    public static Hex fromDTO(HexDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Hex.class);
    }


    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        // hex has to have correct coordinates
        if (getQ() + getR() + getS() != 0) {
            errors.add("Hex (%d, %d, %d) has to have correct coordinates!".formatted(getQ(), getR(), getS()));
        }

        return errors;
    }

    @Override
    public String toString() {
        return "Hex (%d, %d, %d)".formatted(getQ(), getR(), getS());
    }
}
