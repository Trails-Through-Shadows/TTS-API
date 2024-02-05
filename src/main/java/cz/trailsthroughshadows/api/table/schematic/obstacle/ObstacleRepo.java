package cz.trailsthroughshadows.api.table.schematic.obstacle;

import cz.trailsthroughshadows.api.table.enemy.model.dto.EnemyDTO;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.ObstacleDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ObstacleRepo extends JpaRepository<ObstacleDTO, Integer> {

    @Override
    @EntityGraph(attributePaths = {"effects"})
    List<ObstacleDTO> findAll();

    @Query("select o " +
            "from ObstacleDTO o " +
            "join HexObstacleDTO ho on o.id = ho.key.idObstacle " +
            "where ho.key.idLocation = ?1")
    List<EnemyDTO> findAllByLocationId(int id);

}
