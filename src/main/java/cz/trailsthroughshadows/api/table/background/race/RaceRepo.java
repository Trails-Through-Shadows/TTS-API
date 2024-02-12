package cz.trailsthroughshadows.api.table.background.race;

import cz.trailsthroughshadows.api.table.background.race.model.RaceDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface RaceRepo extends JpaRepository<RaceDTO, Integer> {

    @Query("SELECT r FROM RaceDTO r")
    Collection<RaceDTO> getAll();
}
