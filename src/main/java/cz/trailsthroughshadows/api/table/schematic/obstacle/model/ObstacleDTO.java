package cz.trailsthroughshadows.api.table.schematic.obstacle.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsFilter;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.forothers.ObstacleEffectDTO;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Obstacle")
@EqualsAndHashCode(callSuper = true)
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
public class ObstacleDTO extends Validable {

    @Id
    public Integer id;

    @Column(nullable = false, length = 128)
    public String title;

    @Column(length = 32)
    private String tag;

    @Column
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
    public List<ObstacleEffectDTO> effects;

    public List<EffectDTO> getMappedEffects() {
        if (effects == null) return null;
        return effects.stream().map(ObstacleEffectDTO::getEffect).toList();
    }

    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // TODO: Implement validation
    }

    @Override
    public String getValidableValue() {
        return null;
    }
}
