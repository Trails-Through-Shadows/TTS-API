package cz.trailsthroughshadows.api.table.schematic.part;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface PartRepo extends JpaRepository<Part, Integer> {

    @Override
    @EntityGraph(attributePaths = {"hexes"})
    List<Part> findAll();


    @Query("SELECT p FROM Part p")
    List<Part> findWithoutHexes();

}
