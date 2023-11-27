package cz.trailsthroughshadows.api.summon;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface SummonRepo extends JpaRepository<SummonModel,Integer> {

    @Query("SELECT c FROM SummonModel c")
    Collection<SummonModel> getAll();
}
