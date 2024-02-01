package cz.trailsthroughshadows.algorithm.validation;

import cz.trailsthroughshadows.algorithm.location.Navigation;
import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;

public class PartValidation {
    public static List<String> validate(Part part) {
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

        // every hex has to have correct coordinates
        for (Hex hex : part.getHexes()) {
            errors.addAll(HexValidation.validate(hex));
        }
        if (!errors.isEmpty())
            return errors;

        // max 8 hexes wide
        List<Integer> coordinates = new ArrayList<>();

        IntSummaryStatistics qStats = part.getHexes().stream().mapToInt(Hex::getQ).summaryStatistics();
        IntSummaryStatistics rStats = part.getHexes().stream().mapToInt(Hex::getR).summaryStatistics();
        IntSummaryStatistics sStats = part.getHexes().stream().mapToInt(Hex::getS).summaryStatistics();

        int diffQ = qStats.getMax() - qStats.getMin();
        int diffR = rStats.getMax() - rStats.getMin();
        int diffS = sStats.getMax() - sStats.getMin();

        if (diffQ >  maxHexesWide || diffR > maxHexesWide || diffS > maxHexesWide) {
            errors.add("Part must not be wider than %d hexes!".formatted(maxHexesWide));
        }

        // no hexes can be on the same position
        int duplicates = 0;
        for (Hex hex1 : part.getHexes()) {
            for (Hex hex2 : part.getHexes()) {
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

