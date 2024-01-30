package cz.trailsthroughshadows.api.rest.endpoints;

import cz.trailsthroughshadows.algorithm.location.Navigation;
import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import cz.trailsthroughshadows.api.table.schematic.part.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ValidationService {

    // TODO: Implement Part validation @Bačkorče
    // Validations?:
    // 1. Part must have at least 5 hexes
    // 2. Part must have at most 50 hexes
    // 3. Part is maximum 8 hexes wide and 8 hexes tall
    // 4. All hexes must be connected
    public List<String> validatePart(Part part) {
        log.info("2 Validating part " + part.getTag());
        System.out.println("2 Validating part " + part.getTag());
        List<String> errors = new ArrayList<>();

        int minHexes = 5;
        int maxHexes = 50;
        int maxHexesWide = 8;

        // min 5 hexes
        if (part.getHexes().size() < minHexes) {
            errors.add("Part must have at least %d hexes!".formatted(minHexes));
        }

        // max 50 hexes
        if (part.getHexes().size() > maxHexes) {
            errors.add("Part must have at most 50 hexes!");
        }

        // max 8 hexes wide
        int diffQ = part.getHexes().stream().mapToInt(Hex::getQ).max().getAsInt() - part.getHexes().stream().mapToInt(Hex::getQ).min().getAsInt();
        int diffR = part.getHexes().stream().mapToInt(Hex::getR).max().getAsInt() - part.getHexes().stream().mapToInt(Hex::getR).min().getAsInt();
        int diffS = part.getHexes().stream().mapToInt(Hex::getS).max().getAsInt() - part.getHexes().stream().mapToInt(Hex::getS).min().getAsInt();
        if (diffQ >  maxHexesWide || diffR > maxHexesWide || diffS > maxHexesWide) {
            errors.add("Part must not be wider than %d hexes!".formatted(maxHexesWide));
        }

        // no hexes can be on the same position
        for (Hex hex1 : part.getHexes()) {
            for (Hex hex2 : part.getHexes()) {
                if (hex1 == hex2)
                    continue;

                if (hex1.getQ() == hex2.getQ() && hex1.getR() == hex2.getR() && hex1.getS() == hex2.getS()) {
                    errors.add("No hexes can be on the same position!");
                    break;
                }
            }
        }

        // every hex has to have correct coordinates
        for (Hex hex : part.getHexes()) {
            if (hex.getQ() + hex.getR() + hex.getS() != 0) {
                errors.add("Every hex has to have correct coordinates!");
                break;
            }
        }

        // must include center hex
        Optional<Hex> centerHex = part.getHexes().stream().filter(hex -> hex.getQ() == 0 && hex.getR() == 0 && hex.getS() == 0).findFirst();
        if (centerHex.isEmpty()) {
            errors.add("Part must include a center hex!");
            return errors;
        }

        // all hexes must be connected
        Navigation navigation = new Navigation(part);

        for (Hex hex : part.getHexes()) {
            if (hex == centerHex.get())
                continue;

            if (navigation.getPath(centerHex.get(), hex) == null) {
                errors.add("All hexes must be connected!");
                break;
            }
        }

        return errors;
    }
}
