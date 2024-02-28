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
        if (duration == null) {
            errors.add(new ValidationError("Effect", "duration", null, "Duration must not be null."));
        }
        if (target == null) {
            errors.add(new ValidationError("Effect", "target", null, "Target must not be null."));
        }
        if (type == null) {
            errors.add(new ValidationError("Effect", "type", null, "Type must not be null."));
            return;
        }

        // Description has to be valid.
        Description description = new Description(getDescription());
        validateChild(description, validationConfig);

        // Duration must be greater than 0 or exactly -1 (infinity).
        List<EffectDTO.EffectType> instant = List.of(EffectType.PUSH, EffectType.PULL, EffectType.HEAL);
        if (duration != null && instant.contains(type) && duration != 0) {
            errors.add(new ValidationError("Effect", "duration", getDuration(), "Duration must be 0 for this type of effect."));
        } else if (duration != null && !instant.contains(type) && duration < 1 && duration != -1) {
            errors.add(new ValidationError("Effect", "duration", getDuration(), "Duration must be greater than 0 or -1 (infinity)."));
        }

        // Strength must be positive. It must be null for types Disarm, Root, Stun, Confusion, Guidance and Incorporeal. It can be null for resistances.
        List<EffectDTO.EffectType> noStrength = List.of(EffectType.DISARM, EffectType.ROOT, EffectType.STUN, EffectType.CONFUSION, EffectType.GUIDANCE, EffectType.INCORPOREAL);
        List<EffectDTO.EffectType> resistances = List.of(EffectType.FORCED_MOVEMENT_RESISTANCE, EffectType.POISON_RESISTANCE, EffectType.FIRE_RESISTANCE, EffectType.BLEED_RESISTANCE, EffectType.DISARM_RESISTANCE, EffectType.ROOT_RESISTANCE, EffectType.STUN_RESISTANCE, EffectType.CONFUSION_RESISTANCE, EffectType.WEAKNESS_RESISTANCE, EffectType.ENFEEBLE_RESISTANCE, EffectType.SLOW_RESISTANCE, EffectType.CONSTRAIN_RESISTANCE);
        if (noStrength.contains(type) && strength != null) {
            errors.add(new ValidationError("Effect", "strength", getStrength(), "Strength must be null for this type of effect."));
        } else if (strength == null) {
            errors.add(new ValidationError("Effect", "strength", null, "Strength must not be null for this type of effect."));
        } else if (resistances.contains(type) && strength < 0 && strength != -1) {
            errors.add(new ValidationError("Effect", "strength", getStrength(), "Strength must be greater than or equal to 0 for this type of effect."));
        } else if (strength < 1) {
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
        WEAKNESS,
        WEAKNESS_RESISTANCE,
        HEAL,
        REGENERATION,
        EMPOWER,
        ENFEEBLE,
        ENFEEBLE_RESISTANCE,
        SPEED,
        SLOW,
        SLOW_RESISTANCE,
        REACH,
        CONSTRAIN,
        CONSTRAIN_RESISTANCE,
        INCORPOREAL,
        BONUS_HEALTH,
        BONUS_DEFENCE,
        BONUS_INITIATIVE
    }


    public enum EffectTarget implements Serializable {
        SELF,
        ONE,
        ALL,
        ALL_ALLIES,
        ALL_ENEMIES,
    }
}
