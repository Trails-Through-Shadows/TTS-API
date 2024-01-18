package cz.trailsthroughshadows.api.table.schematic.obstacle;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ObstacleRepo extends JpaRepository<Obstacle, Integer> {

    @Query("SELECT c FROM Obstacle c")
    Collection<Obstacle> getOnlyObstacles();

    @Override
    @EntityGraph(attributePaths = {"effects"})
    List<Obstacle> findAll();

}
