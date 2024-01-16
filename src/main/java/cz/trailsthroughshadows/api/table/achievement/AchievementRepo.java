package cz.trailsthroughshadows.api.table.achievement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface AchievementRepo extends JpaRepository<Achievement, Integer> {

    //get all
    @Query("SELECT c FROM Achievement c")
    Collection<Achievement> getAll();

}
