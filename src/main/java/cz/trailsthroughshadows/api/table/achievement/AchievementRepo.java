package cz.trailsthroughshadows.api.table.achievement;

import cz.trailsthroughshadows.api.table.summon.Summon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepo extends JpaRepository<Summon,Integer> {

}
