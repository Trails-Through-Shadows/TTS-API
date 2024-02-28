package cz.trailsthroughshadows.api.table.playerdata.adventure.achievement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface AchievementRepo extends JpaRepository<AchievementDTO, Integer> {

    // get all
    @Query("SELECT c FROM AchievementDTO c")
    Collection<AchievementDTO> getAll();

}
