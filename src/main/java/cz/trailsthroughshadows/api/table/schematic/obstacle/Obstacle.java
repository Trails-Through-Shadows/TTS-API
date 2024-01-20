package cz.trailsthroughshadows.api.table.schematic.obstacle;

import cz.trailsthroughshadows.api.table.effect.Effect;
import cz.trailsthroughshadows.api.table.effect.forothers.ObstacleEffect;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Obstacle")
// Someday we will foresee obstacles
// Through the blizzard, through the blizzard
public class Obstacle {

    @Id
    public Integer id;

    @Column(nullable = false, length = 50)
    public String name;

    @Column
    public Integer damage;

    @Column
    public Integer health;

    @Column(nullable = false)
    public boolean crossable;

    @Column
    public Integer usages = 0;

    @OneToMany(mappedBy = "idObstacle", fetch = FetchType.LAZY)
    @ToString.Exclude
    public Set<ObstacleEffect> effects;

    @ToString.Include(name = "effects")
    public Collection<Effect> getEffects() {
        if (effects == null) return null;
        return effects.stream().map(ObstacleEffect::getEffect).toList();
    }

}
