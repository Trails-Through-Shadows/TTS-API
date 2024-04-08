package cz.trailsthroughshadows.api.table.action.features.skill;
import cz.trailsthroughshadows.api.table.action.features.movement.Movement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepo extends JpaRepository<Skill, Integer> {
}
