package cz.trailsthroughshadows.api.table.effect.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Description;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import io.swagger.v3.core.util.Json;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
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

    // region Validation
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
            errors.add(new ValidationError("Effect", "duration", getDuration(),
                    "Duration must be 0 for this type of effect."));
        } else if (duration != null && !instant.contains(type) && duration < 1 && duration != -1) {
            errors.add(new ValidationError("Effect", "duration", getDuration(),
                    "Duration must be greater than 0 or -1 (infinity)."));
        }

        // Strength must be positive. It must be null for types Disarm, Root, Stun,
        // Confusion, Guidance and Incorporeal. It can be null for resistances.
        List<EffectDTO.EffectType> noStrength = List.of(EffectType.STUN,
                EffectType.CONFUSION, EffectType.GUIDANCE);
        List<EffectDTO.EffectType> resistances = List.of(EffectType.FORCED_MOVEMENT_RESISTANCE,
                EffectType.POISON_RESISTANCE, EffectType.FIRE_RESISTANCE, EffectType.BLEED_RESISTANCE,
                EffectType.STUN_RESISTANCE, EffectType.CONFUSION_RESISTANCE,
                EffectType.ENFEEBLE_RESISTANCE,
                EffectType.SLOW_RESISTANCE);
        if (noStrength.contains(type) && strength != null) {
            errors.add(new ValidationError("Effect", "strength", getStrength(),
                    "Strength must be null for this type of effect."));
        } else if (strength == null) {
            errors.add(new ValidationError("Effect", "strength", null,
                    "Strength must not be null for this type of effect."));
        } else if (resistances.contains(type) && strength < 0 && strength != -1) {
            errors.add(new ValidationError("Effect", "strength", getStrength(),
                    "Strength must be greater than or equal to 0 for this type of effect."));
        } else if (strength < 1) {
            errors.add(new ValidationError("Effect", "strength", getStrength(), "Strength must be greater than 0."));
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
        SPEED("Speed", true, true, false),
        SLOW("Slow", true, true, false),
        SLOW_RESISTANCE("Slow", true, true, true),
        GUIDANCE("Guidance", false, true, false),
        CONFUSION("Confusion", false, true, false),
        CONFUSION_RESISTANCE("Confusion", false, true, true),
        PROTECTION("Protection", true, true, false);

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
