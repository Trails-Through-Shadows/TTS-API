package cz.trailsthroughshadows.api.table.action.features.attack;
import cz.trailsthroughshadows.api.table.action.features.movement.Movement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttackRepo extends JpaRepository<Attack, Integer> {
}
