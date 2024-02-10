package cz.trailsthroughshadows.api.table.schematic.obstacle.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.api.rest.jsonfilter.LazyFieldsFilter;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.forothers.ObstacleEffect;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Obstacle")
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
public class ObstacleDTO {

    @Id
    public Integer id;

    @Column(nullable = false, length = 128)
    public String title;
    @Column(length = 32)
    private String tag;
    @Column(nullable = true)
    public String description;

    @Column
    public Integer baseDamage;

    @Column
    public Integer baseHealth;

    @Column(nullable = false)
    public boolean crossable;
    @Column
    public Integer usages = 0;
    @OneToMany(mappedBy = "idObstacle", fetch = FetchType.LAZY)
    public Set<ObstacleEffect> effects;

    public Collection<EffectDTO> getEffects() {
        if (effects == null) return null;
        return effects.stream().map(ObstacleEffect::getEffect).toList();
    }
}
