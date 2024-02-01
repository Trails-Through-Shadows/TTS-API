package cz.trailsthroughshadows.algorithm.validation;

import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class HexValidation {

    public static List<String> validate(Hex hex) {
        List<String> errors = new ArrayList<>();

        // hex has to have correct coordinates
        if (hex.getQ() + hex.getR() + hex.getS() != 0) {
            errors.add("Hex (%d, %d, %d) has to have correct coordinates!".formatted(hex.getQ(), hex.getR(), hex.getS()));
        }

        return errors;
    }
}
