package cz.trailsthroughshadows.api.table.enemy;


import cz.trailsthroughshadows.api.table.enemy.model.dto.EnemyDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EnemyRepo extends JpaRepository<EnemyDTO, Integer> {

    @Override
    @EntityGraph(attributePaths = {"actions"})
    List<EnemyDTO> findAll();

    @Override
    @EntityGraph(attributePaths = {"actions"})
    Optional<EnemyDTO> findById(Integer id);

    @Query("SELECT c FROM EnemyDTO c")
    Collection<EnemyDTO> getAll();


    @Query("SELECT c FROM EnemyDTO c where c.id = ?1")
    Optional<EnemyDTO> getById(int id);


    @Query("select c " +
            "from EnemyDTO c " +
            "join HexEnemyDTO he on c.id = he.key.idEnemy " +
            "where he.key.idLocation = ?1")
    List<EnemyDTO> findAllByLocationId(int id);
}
