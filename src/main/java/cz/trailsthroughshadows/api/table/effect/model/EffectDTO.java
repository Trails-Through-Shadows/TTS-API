package cz.trailsthroughshadows.api.table.effect.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

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
        // TODO
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
        STUN,
        STUN_RESISTANCE,
        CONFUSION,
        CONFUSION_RESISTANCE,
        CHARM,
        CHARM_RESISTANCE,
        FEAR,
        FEAR_RESISTANCE,
        INVISIBILITY,
        SHIELD,
        HEAL,
        REGENERATION,
        BONUS_HEALTH,
        BONUS_DAMAGE,
        BONUS_MOVEMENT,
    }

    public enum EffectTarget implements Serializable {
        SELF,
        ONE,
        ALL,
        ALL_ALLIES,
        ALL_ENEMIES,
    }
}
