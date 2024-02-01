package cz.trailsthroughshadows.algorithm.location;

import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import cz.trailsthroughshadows.api.table.schematic.location.ILocation;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;

public abstract class LocationImpl implements ILocation {

    public Part getPart(Hex hex) {
        return getParts().stream()
                .filter(part -> part.getId() == hex.getKey().getIdPart())
                .findFirst()
                .orElse(null);
    }
}
