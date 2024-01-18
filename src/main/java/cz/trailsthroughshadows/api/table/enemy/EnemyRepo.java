package cz.trailsthroughshadows.api.table.enemy;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface EnemyRepo extends JpaRepository<Enemy, Integer> {

    @Query("SELECT c FROM Enemy c")
    Collection<Enemy> getAll();
}
