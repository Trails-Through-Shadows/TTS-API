package cz.trailsthroughshadows.api.table.action.features.summon;


import cz.trailsthroughshadows.api.table.action.features.summon.model.SummonDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface SummonRepo extends JpaRepository<SummonDTO, Integer> {

    @Query("SELECT c FROM SummonDTO c")
    Collection<SummonDTO> getAll();
}
