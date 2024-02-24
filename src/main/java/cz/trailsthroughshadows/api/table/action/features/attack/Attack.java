package cz.trailsthroughshadows.api.table.action.features.attack;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.foraction.AttackEffect;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "Attack")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Attack extends Validable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(nullable = false)
    private int range;

    @Column(nullable = false)
    private int damage;

    @Column(nullable = false)
    private Integer area;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EffectDTO.EffectTarget target;

    @Column(nullable = false)
    private int numAttacks;

    @OneToMany(mappedBy = "key.idAttack", fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private Collection<AttackEffect> effects;
    
    @JsonIgnore
    public Collection<EffectDTO> getMappedEffects() {
        if (effects == null) return null;
        return effects.stream().map(AttackEffect::getEffect).toList();
    }

    //region Validation
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Target cant be null.
        if (target == null) {
            errors.add(new ValidationError("Attack", "target", null, "Target must not be null."));
        }

        // Range must be greater than 0. It can be 0 only if target is SELF.
        if (range < 0 || (range == 0 && target != EffectDTO.EffectTarget.SELF)) {
            errors.add(new ValidationError("Attack", "range", getRange(), "Range must be greater than 0."));
        }

        // Area of effect must be greater than or equal to 0.
        if (area < 0) {
            errors.add(new ValidationError("Attack", "area", getArea(), "Area of effect must be greater than 0."));
        }

        // Damage must not be negative.
        if (damage < 0) {
            errors.add(new ValidationError("Attack", "damage", getDamage(), "Damage must not be negative."));
        }

        // Number of attacks must be greater than 0.
        if (numAttacks <= 0) {
            errors.add(new ValidationError("Attack", "numAttacks", getNumAttacks(), "Number of attacks must be greater than 0."));
        }

        // All effects must be validated.
        for (var e : getMappedEffects()) {
            if (e == null) {
                errors.add(new ValidationError("Skill", "effects", null, "Skill must not contain null effects."));
                break;
            }
            validateChild(e, validationConfig);
        }
    }

    @Override
    public String getValidableValue() {
        return getDamage() + " (" + getNumAttacks() + "x) within " + getRange() + " hexes.";
    }
    //endregion
}
