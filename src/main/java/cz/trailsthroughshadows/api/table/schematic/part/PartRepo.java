package cz.trailsthroughshadows.api.table.schematic.part;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PartRepo extends JpaRepository<Part, Integer> {

    @Override
    @EntityGraph(attributePaths = {"hexes", "doors"})
    List<Part> findAll();

    @Override
    @EntityGraph(attributePaths = {"hexes", "doors"})
    Optional<Part> findById(Integer id);


    @Query("SELECT p FROM Part p")
    List<Part> findWithoutHexes();

}
