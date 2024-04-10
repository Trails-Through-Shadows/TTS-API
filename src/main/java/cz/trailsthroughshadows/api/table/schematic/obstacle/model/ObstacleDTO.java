package cz.trailsthroughshadows.api.table.schematic.obstacle.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Description;
import cz.trailsthroughshadows.algorithm.validation.text.Tag;
import cz.trailsthroughshadows.algorithm.validation.text.Title;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.forothers.ObstacleEffectDTO;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Obstacle")
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ObstacleDTO extends Validable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "key.idObstacle", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    public List<ObstacleEffectDTO> effects;

    @JsonIgnore
    public List<EffectDTO> getMappedEffects() {
        if (effects == null)
            return null;
        return effects.stream().map(ObstacleEffectDTO::getEffect).toList();
    }

    // region Validation
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Title, tag and description have to be valid.
        validateChild(new Title(getTitle()), validationConfig);
        validateChild(new Tag(getTag()), validationConfig);
        validateChild(new Description(getDescription()), validationConfig);

        // BaseDamage must be greater than or equal to 0. If it is not crossable, it
        // must be 0.
        if (baseDamage == null) {
            errors.add(new ValidationError("Obstacle", "baseDamage", null, "Base damage must not be null."));
        } else if (!crossable && baseDamage != 0) {
            errors.add(new ValidationError("Obstacle", "baseDamage", getBaseDamage(),
                    "Base damage must be 0 if obstacle is not crossable."));
        } else if (baseDamage < 0) {
            errors.add(new ValidationError("Obstacle", "baseDamage", getBaseDamage(),
                    "Base damage must be greater than or equal to 0."));
        }

        // BaseHealth must be greater than 0 or -1 (invincible).
        if (baseHealth == null) {
            errors.add(new ValidationError("Obstacle", "baseHealth", null, "Base health must not be null."));
        } else if (baseHealth != -1 && baseHealth <= 0) {
            errors.add(new ValidationError("Obstacle", "baseHealth", getBaseHealth(),
                    "Base health must be greater than 0 or -1 (invincible)."));
        }

        // All effects must be validated.
        for (var effect : getMappedEffects()) {
            validateChild(effect, validationConfig);
        }
    }

    @Override
    public String getValidableValue() {
        return getTitle();
    }
    // endregion
}
