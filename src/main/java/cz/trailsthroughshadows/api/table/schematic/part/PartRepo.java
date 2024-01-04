package cz.trailsthroughshadows.api.table.schematic.part;

import cz.trailsthroughshadows.api.table.schematic.hex.HexDoor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PartRepo extends JpaRepository<Part, Integer> {

    @Override
    @EntityGraph(attributePaths = {"hexes", "firstPart"})
    List<Part> findAll();

    @Query("SELECT h FROM HexDoor h WHERE :id = h.firstPart OR :id = h.secondPart")
    List<HexDoor> getHexDoors(@Param("id") int idPart);

    @Query("SELECT p FROM Part p")
    List<Part> findWithoutHexes();

}
