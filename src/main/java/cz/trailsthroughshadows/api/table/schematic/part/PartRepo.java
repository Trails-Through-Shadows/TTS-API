package cz.trailsthroughshadows.api.table.schematic.part;


import cz.trailsthroughshadows.api.table.schematic.part.model.PartDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartRepo extends JpaRepository<PartDTO, Integer> {

    @Override
    @EntityGraph(attributePaths = {"hexes"})
    List<PartDTO> findAll();

    @Override
    @EntityGraph(attributePaths = {"hexes"})
    Optional<PartDTO> findById(Integer id);

}
