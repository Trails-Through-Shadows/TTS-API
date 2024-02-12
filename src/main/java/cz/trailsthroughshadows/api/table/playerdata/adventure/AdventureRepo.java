package cz.trailsthroughshadows.api.table.playerdata.adventure;

import cz.trailsthroughshadows.api.table.playerdata.adventure.model.AdventureDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdventureRepo extends JpaRepository<AdventureDTO, Integer> {

}
