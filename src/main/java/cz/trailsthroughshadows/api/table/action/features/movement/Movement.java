package cz.trailsthroughshadows.api.table.action.features.movement;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.foraction.MovementEffect;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity
@Table(name = "Movement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Movement extends Validable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(nullable = false)
    private int range;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MovementType type = MovementType.WALK;

    @OneToMany(mappedBy = "idMovement", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Collection<MovementEffect> effects;

    // Skipping n-to-n relationship, there is no additional data in that table
    @ToString.Include(name = "effects") // Including replacement field in toString
    public Collection<EffectDTO> getEffects() {
        if (effects == null) return null;
        return effects.stream().map(MovementEffect::getEffect).toList();
    }

    //region Validation
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Range must be greater than 0.
        if (range <= 0) {
            errors.add(new ValidationError("SummonAction", "range", getRange(), "Range must be greater than 0."));
        }
    }

    @Override
    public String getValidableValue() {
        return getType().name() + " (" + getRange() + ")";
    }
    //endregion

    enum MovementType {
        WALK,
        JUMP,
    }
}
