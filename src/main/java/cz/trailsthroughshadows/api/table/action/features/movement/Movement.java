package cz.trailsthroughshadows.api.table.action.features.movement;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
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
    private Integer range;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MovementType type = MovementType.WALK;

    @OneToMany(mappedBy = "key.idMovement", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private Collection<MovementEffect> effects;

    // Skipping n-to-n relationship, there is no additional data in that table
    //@ToString.Include(name = "effects") // Including replacement field in toString
    @JsonIgnore
    public Collection<EffectDTO> getMappedEffects() {
        if (effects == null) return null;
        return effects.stream().map(MovementEffect::getEffect).toList();
    }

    //region Validation
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Type cant be null.
        if (type == null) {
            errors.add(new ValidationError("Movement", "type", null, "Type must not be null."));
        }
        // Range cant be null.
        if (range == null) {
            errors.add(new ValidationError("Movement", "range", null, "Range must not be null."));
        }

        if (!errors.isEmpty()) return;

        // Range must be greater than 0.
        if (range <= 0) {
            errors.add(new ValidationError("Movement", "range", getRange(), "Range must be greater than 0."));
        }

        // All effects must be validated.
        for (var e : effects) {
            validateChild(e.getEffect(), validationConfig);
        }
    }

    @Override
    public String getValidableValue() {
        return null;
    }
    //endregion

    enum MovementType {
        WALK,
        JUMP,
    }
}
