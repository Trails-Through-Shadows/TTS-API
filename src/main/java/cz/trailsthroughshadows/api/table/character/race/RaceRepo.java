package cz.trailsthroughshadows.api.table.character.race;

import cz.trailsthroughshadows.api.table.character.clazz.Clazz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface RaceRepo extends JpaRepository<Race, Integer> {

    @Query("SELECT r FROM Race r")
    Collection<Clazz> getAll();
}
