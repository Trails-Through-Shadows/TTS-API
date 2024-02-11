package cz.trailsthroughshadows.api.table.action.features.restorecards;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "RestoreCards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RestoreCards extends Validable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column
    private Integer numCards;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EffectDTO.EffectTarget target;

    @Column
    private Boolean random;

    //region Validation
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Number of cards must be greater than 0 or exactly -1 (infinity).
        if (numCards < 0 && numCards != -1) {
            errors.add(new ValidationError("RestoreCards", "numCards", numCards, "Number of cards must be greater than 0 or exactly -1 (infinity)."));
        }
    }

    @Override
    public String getValidableValue() {
        return (numCards == -1 ? "all" : numCards) + " cards to " + target.name() + (random ? " randomly." : ".");
    }
    //endregion
}
