package cz.trailsthroughshadows.api.table.schematic.obstacle;

import cz.trailsthroughshadows.api.table.schematic.obstacle.model.ObstacleDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObstacleRepo extends JpaRepository<ObstacleDTO, Integer> {

    @Override
    @EntityGraph(attributePaths = {"effects"})
    List<ObstacleDTO> findAll();

}
