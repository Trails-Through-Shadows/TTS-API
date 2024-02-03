package cz.trailsthroughshadows.algorithm.location;

import cz.trailsthroughshadows.algorithm.util.Vec3;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexDTO;
import cz.trailsthroughshadows.api.table.schematic.part.model.PartDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class Navigation {

    private final List<PartDTO> parts;

    public Navigation(PartDTO... parts) {
        this.parts = Arrays.asList(parts);
    }

    public PartDTO getPart(HexDTO hex) {
        return parts.stream()
                .filter(part -> part.getId() == hex.getKey().getIdPart())
                .findFirst()
                .orElse(null);
    }

    public int getDistance(HexDTO hex1, HexDTO hex2) {
        Vec3<Integer> vec = new Vec3<>(hex1.getQ() - hex2.getQ(), hex1.getR() - hex2.getR(), hex1.getS() - hex2.getS());
        return (Math.abs(vec.x()) + Math.abs(vec.y()) + Math.abs(vec.z())) / 2;
    }

    public List<HexDTO> getNeighbors(HexDTO hex) {
        return getNeighbors(hex, 1);
    }

    public List<HexDTO> getNeighbors(HexDTO hex, int range) {
        return getPart(hex).getHexes().stream()
                .filter(neighbor -> hex != neighbor && getDistance(hex, neighbor) <= range)
                .toList();
    }

    public List<HexDTO> getPath(HexDTO hex1, HexDTO hex2) {
        Map<HexDTO, Integer> distances = new HashMap<>();
        Queue<HexDTO> queue = new LinkedList<>();
        List<HexDTO> path = new ArrayList<>();

        distances.put(hex1, 0);
        queue.add(hex1);

        // calculate distance from hex1
        while (!queue.isEmpty()) {
            HexDTO current = queue.poll();
            int distance = distances.get(current);

            List<HexDTO> neighbors = getNeighbors(current);
            for (HexDTO neighbor : neighbors) {
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
        HexDTO currentHex = hex2;
        int currentDistance = distances.get(hex2);

        while (!currentHex.equals(hex1)) {
            path.add(currentHex);

            List<HexDTO> neighbors = getNeighbors(currentHex);
            for (HexDTO neighbor : neighbors) {
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
