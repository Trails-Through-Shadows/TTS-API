package cz.trailsthroughshadows.algorithm.location;

import cz.trailsthroughshadows.algorithm.entity.Entity;
import cz.trailsthroughshadows.algorithm.utils.Vec3;
import cz.trailsthroughshadows.api.table.effect.Effect;
import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import cz.trailsthroughshadows.api.table.schematic.location.ILocation;
import cz.trailsthroughshadows.api.table.schematic.part.Part;

import java.util.ArrayList;
import java.util.List;

public abstract class LocationImpl implements ILocation {

    public Part getPart(Hex hex) {
        return getLocationParts().stream()
            .filter(part -> part.getId() == hex.getKey().getIdPart())
            .findFirst()
            .orElse(null);
    }

    public int getDistance(Hex hex1, Hex hex2) {
        Vec3<Integer> vec = new Vec3<>(hex1.getQ() - hex2.getQ(), hex1.getR() - hex2.getR(), hex1.getS() - hex2.getS());
        return (Math.abs(vec.x()) + Math.abs(vec.y()) + Math.abs(vec.z())) / 2;
    }

    public List<Hex> getNeighbors(Hex hex) {
        return getNeighbors(hex, 1);
    }
    public List<Hex> getNeighbors(Hex hex, int range) {
        List<Hex> neighbors = getPart(hex).getHexes().stream()
            .filter(neighbor -> hex != neighbor && getDistance(hex, neighbor) <= range)
            .toList();

        return neighbors;
    }
}
