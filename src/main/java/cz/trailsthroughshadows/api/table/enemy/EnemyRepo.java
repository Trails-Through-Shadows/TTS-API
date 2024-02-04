package cz.trailsthroughshadows.api.table.enemy;


import cz.trailsthroughshadows.api.table.enemy.model.dto.EnemyDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface EnemyRepo extends JpaRepository<EnemyDTO, Integer> {

    @Query("SELECT c FROM EnemyDTO c")
    Collection<EnemyDTO> getAll();

    @Query("SELECT c FROM EnemyDTO c WHERE c.title = ?1")
    EnemyDTO getByTitle(String name);
}
