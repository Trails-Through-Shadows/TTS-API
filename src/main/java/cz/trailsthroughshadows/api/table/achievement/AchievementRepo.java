package cz.trailsthroughshadows.api.table.achievement;

import cz.trailsthroughshadows.api.table.action.summon.Summon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface AchievementRepo extends JpaRepository<Summon,Integer> {

    //get all
    @Query("SELECT c FROM Summon c")
    Collection<Summon> getAll();

}
