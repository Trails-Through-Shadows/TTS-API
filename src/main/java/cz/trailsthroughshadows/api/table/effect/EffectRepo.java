package cz.trailsthroughshadows.api.table.effect;

import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface EffectRepo extends JpaRepository<EffectDTO, Integer> {
    //find all
    @Query("SELECT a FROM EffectDTO a")
    Collection<EffectDTO> getAll();

    // Find by target, type, duration, strength
    @Query("SELECT a FROM EffectDTO a WHERE a.target = ?1 AND a.type = ?2 AND a.duration = ?3 AND a.strength = ?4")
    List<EffectDTO> findUnique(EffectDTO.EffectTarget target, EffectDTO.EffectType type, Integer duration, Integer strength);

}
