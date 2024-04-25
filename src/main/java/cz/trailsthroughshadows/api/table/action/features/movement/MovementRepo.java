package cz.trailsthroughshadows.api.table.action.features.movement;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MovementRepo extends JpaRepository<MovementDTO, Integer> {
}
