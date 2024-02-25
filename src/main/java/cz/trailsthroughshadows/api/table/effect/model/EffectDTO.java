package cz.trailsthroughshadows.api.table.effect.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Description;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Effect")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EffectDTO extends Validable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @Enumerated(EnumType.STRING)
    private EffectTarget target;

    @Column
    @Enumerated(EnumType.STRING)
    private EffectType type;

    @Column
    private Integer duration;

    @Column
    private Integer strength;

    @Column
    private String description;

    //region Validation
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Type, duration and target cant be null.
        if (type == null) {
            errors.add(new ValidationError("Effect", "type", null, "Type must not be null."));
        }
        if (duration == null) {
            errors.add(new ValidationError("Effect", "duration", null, "Duration must not be null."));
        }
        if (target == null) {
            errors.add(new ValidationError("Effect", "target", null, "Target must not be null."));
        }

        if (!errors.isEmpty()) return;

        // Description has to be valid.
        Description description = new Description(getDescription());
        validateChild(description, validationConfig);

        // Duration must be greater than 0 or exactly -1 (infinity).
        if (duration < 1 && duration != -1) {
            errors.add(new ValidationError("Effect", "duration", getDuration(), "Duration must be greater than 0 or -1 (infinity)."));
        }

        // Strength must be positive. It must be null for types Disarm, Root, Stun, Confusion, Guidance and Incorporeal.
        List<EffectDTO.EffectType> noStrength = List.of(EffectType.DISARM, EffectType.ROOT, EffectType.STUN, EffectType.CONFUSION, EffectType.GUIDANCE, EffectType.INVINCIBILITY, EffectType.SHIELD);
        if (noStrength.contains(type) && strength != null) {
            errors.add(new ValidationError("Effect", "strength", getStrength(), "Strength must be null for this type of effect."));
        } else if (strength != null && strength < 1) {
            errors.add(new ValidationError("Effect", "strength", getStrength(), "Strength must be greater than 0."));
        }
    }

    @Override
    public String getValidableValue() {
        return type == null ? "Nothing" : getType().toString();
    }
    //endregion

    public enum EffectType implements Serializable {
        PUSH,
        PULL,
        FORCED_MOVEMENT_RESISTANCE,
        POISON,
        POISON_RESISTANCE,
        FIRE,
        FIRE_RESISTANCE,
        BLEED,
        BLEED_RESISTANCE,
        DISARM,
        DISARM_RESISTANCE,
        ROOT,
        ROOT_RESISTANCE,
        STUN,
        STUN_RESISTANCE,
        CONFUSION,
        CONFUSION_RESISTANCE,
        GUIDANCE,
        INVINCIBILITY,
        SHIELD,
        HEAL,
        REGENERATION,
        EMPOWER,
        ENFEEBLE,
        ENFEEBLE_RESISTANCE,
        SPEED,
        SLOW,
        REACH,
    }

    public enum EffectTarget implements Serializable {
        SELF,
        ONE,
        ALL,
        ALL_ALLIES,
        ALL_ENEMIES,
    }
}
