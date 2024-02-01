package cz.trailsthroughshadows.algorithm.location;

import cz.trailsthroughshadows.algorithm.util.Vec3;
import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class Navigation {

    private final List<Part> parts;

    public Navigation(Part... parts) {
        this.parts = Arrays.asList(parts);
    }

    public Part getPart(Hex hex) {
        return parts.stream()
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
        return getPart(hex).getHexes().stream()
                .filter(neighbor -> hex != neighbor && getDistance(hex, neighbor) <= range)
                .toList();
    }

    public List<Hex> getPath(Hex hex1, Hex hex2) {
        Map<Hex, Integer> distances = new HashMap<>();
        Queue<Hex> queue = new LinkedList<>();
        List<Hex> path = new ArrayList<>();

        distances.put(hex1, 0);
        queue.add(hex1);

        // calculate distance from hex1
        while (!queue.isEmpty()) {
            Hex current = queue.poll();
            int distance = distances.get(current);

            List<Hex> neighbors = getNeighbors(current);
            for (Hex neighbor : neighbors) {
                if (distances.containsKey(neighbor))
                    continue;

                distances.put(neighbor, distance + 1);
                queue.add(neighbor);
            }
        }

        // hex2 is not reachable from hex1
        if (!distances.containsKey(hex2))
            return null;

        // create path to hex2
        Hex currentHex = hex2;
        int currentDistance = distances.get(hex2);

        while (!currentHex.equals(hex1)) {
            path.add(currentHex);

            List<Hex> neighbors = getNeighbors(currentHex);
            for (Hex neighbor : neighbors) {
                int neighborDistance = distances.get(neighbor);

                if (neighborDistance < currentDistance) {
                    currentHex = neighbor;
                    currentDistance = neighborDistance;
                    break;
                }
            }
        }
        path.add(hex1);

        Collections.reverse(path);
        return path;
    }
}
