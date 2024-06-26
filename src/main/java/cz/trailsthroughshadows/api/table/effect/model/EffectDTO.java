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

    // region Validation
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Type and target cant be null.
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

        // Duration must be null for effects with no duration.
        // Otherwise, it must not be null and must be greater than 0 or exactly -1 (infinity).
        if (!getType().hasDuration && duration != null) {
            errors.add(new ValidationError("Effect", "duration", getDuration(),
                    "Duration must be null for this type of effect."));
        } else if (getType().hasDuration && duration == null) {
            errors.add(new ValidationError("Effect", "duration", null,
                    "Duration must not be null for this type of effect."));
        } else if (getType().hasDuration && duration < 1 && duration != -1) {
            errors.add(new ValidationError("Effect", "duration", getDuration(),
                    "Duration must be greater than 0, or -1 (infinity)."));
        } else if (getType().hasDuration && duration > 50) {
            errors.add(new ValidationError("Effect", "duration", getDuration(),
                    "Duration must be less than 50."));
        }

        // Strength must be null for effects with no strength.
        // Otherwise, it must not be null and must be greater than 0.
        if (!getType().hasStrength && strength != null) {
            errors.add(new ValidationError("Effect", "strength", getStrength(),
                    "Strength must be null for this type of effect."));
        } else if (getType().hasStrength && strength == null) {
            errors.add(new ValidationError("Effect", "strength", null,
                    "Strength must not be null for this type of effect."));
        } else if ((getType() == EffectType.HEAL || getType() == EffectType.REGENERATION) && strength == -1) {
            // this is okay and im too tired to think
        } else if (getType().isResistance && strength != null && strength == -1) {
            // resistances can have strength -1 too
        } else if (getType().hasStrength && strength < 1) {
            errors.add(new ValidationError("Effect", "strength", getStrength(),
                    "Strength must be greater than 0."));
        } else if (getType().hasStrength && strength > 50) {
            errors.add(new ValidationError("Effect", "strength", getStrength(),
                    "Strength must be less than 50."));
        }
    }

    @Override
    public String getValidableValue() {
        return type == null ? "Nothing" : getType().toString();
    }
    // endregion


    @AllArgsConstructor
    public enum EffectType implements Serializable {
        PUSH("Push", true, false, false),
        PULL("Pull", true, false, false),
        FORCED_MOVEMENT_RESISTANCE("Forced Movement", true, true, true),
        POISON("Poison", true, true, false),
        POISON_RESISTANCE("Poison", true, true, true),
        FIRE("Fire", true, true, false),
        FIRE_RESISTANCE("Fire", true, true, true),
        BLEED("Bleed", true, true, false),
        BLEED_RESISTANCE("Bleed", true, true, true),
        STUN("Stun", false, true, false),
        STUN_RESISTANCE("Stun", false, true, true),
        HEAL("Heal", true, false, false),
        REGENERATION("Regeneration", true, true, false),
        EMPOWER("Empower", true, true, false),
        ENFEEBLE("Enfeeble", true, true, false),
        ENFEEBLE_RESISTANCE("Enfeeble", true, true, true),
        PROTECTION("Protection", true, true, false),
        WEAKNESS("Weakness", true, true, false),
        WEAKNESS_RESISTANCE("Weakness", true, true, true);

        public final String displayName;
        public final boolean hasStrength, hasDuration, isResistance;


        // @JsonCreator
        // public static String forJsonValues() {
        // StringBuilder sb = new StringBuilder();
        // sb.append("[");
        // for (EffectType value : values()) {
        // sb.append("{ \"").append(value).append("\": {");
        // sb.append("\"hasDuration\": ").append(value.hasDuration).append(", ");
        // sb.append("\"hasStrength\": ").append(value.hasStrength).append("}}, ");
        // }
        // return sb.substring(0, sb.length() - 2) + "]";
        // }

    }

    public enum EffectTarget implements Serializable {
        SELF,
        ONE,
        ALL,
        ALL_ALLIES,
        ALL_ENEMIES,
    }
}
