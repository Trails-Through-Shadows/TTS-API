package cz.trailsthroughshadows.api.table.effect;

import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface EffectRepo extends JpaRepository<EffectDTO, Integer> {
    //find all
    @Query("SELECT a FROM EffectDTO a")
    Collection<EffectDTO> getAll();


}
