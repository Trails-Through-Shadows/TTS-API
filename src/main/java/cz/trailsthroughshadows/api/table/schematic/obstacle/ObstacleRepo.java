package cz.trailsthroughshadows.api.table.schematic.obstacle;

import cz.trailsthroughshadows.api.table.schematic.obstacle.model.ObstacleDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ObstacleRepo extends JpaRepository<ObstacleDTO, Integer> {

    @Query("SELECT c FROM ObstacleDTO c")
    Collection<ObstacleDTO> getOnlyObstacles();

    @Override
    @EntityGraph(attributePaths = {"effects"})
    List<ObstacleDTO> findAll();

}
