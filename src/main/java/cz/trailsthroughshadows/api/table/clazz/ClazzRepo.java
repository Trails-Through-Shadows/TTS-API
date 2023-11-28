package cz.trailsthroughshadows.api.table.clazz;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface ClazzRepo extends JpaRepository<Clazz,Integer> {

    @Query("SELECT c FROM Summon c")
    Collection<Clazz> getAll();
}
