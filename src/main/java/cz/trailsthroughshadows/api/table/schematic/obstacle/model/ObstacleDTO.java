package cz.trailsthroughshadows.api.table.schematic.obstacle.model;

import cz.trailsthroughshadows.api.table.effect.Effect;
import cz.trailsthroughshadows.api.table.effect.forothers.ObstacleEffect;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Obstacle")
public class ObstacleDTO {

    @Id
    public Integer id;

    @Column(nullable = false, length = 128)
    public String title;

    @Column
    public Integer damage;

    @Column
    public Integer health;

    @Column(nullable = false)
    public boolean crossable;

    @Column(length = 32)
    private String tag;

    @Column
    public Integer usages = 0;

    @OneToMany(mappedBy = "idObstacle", fetch = FetchType.LAZY)
    public Set<ObstacleEffect> effects;

    public Collection<Effect> getEffects() {
        if (effects == null) return null;
        return effects.stream().map(ObstacleEffect::getEffect).toList();
    }
}
