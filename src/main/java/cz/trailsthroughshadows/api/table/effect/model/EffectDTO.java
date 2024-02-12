package cz.trailsthroughshadows.api.table.effect.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Description;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Effect")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EffectDTO extends Validable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
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

    @Column(nullable = true)
    private String description;

    //region Validation
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Description has to be valid.
        Description description = new Description(getDescription());
        validateChild(description, validationConfig);

        // Duration must be greater than 0 or exactly -1 (infinity).
        if (duration < 1 && duration != -1) {
            errors.add(new ValidationError("Effect", "duration", getDuration(), "Duration must be greater than 0 or -1."));
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
        return getType().toString();
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
