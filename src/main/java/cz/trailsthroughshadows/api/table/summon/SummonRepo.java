package cz.trailsthroughshadows.api.table.summon;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface SummonRepo extends JpaRepository<Summon,Integer> {

    @Query("SELECT c FROM Summon c")
    Collection<Summon> getAll();
}
