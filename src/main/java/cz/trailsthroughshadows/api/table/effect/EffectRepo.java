package cz.trailsthroughshadows.api.table.effect;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface EffectRepo extends JpaRepository<Effect, Integer> {
    //find all
    @Query("SELECT a FROM Effect a")
    Collection<Effect> getAll();


}
