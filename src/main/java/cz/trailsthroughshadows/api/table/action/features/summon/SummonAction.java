package cz.trailsthroughshadows.api.table.action.features.summon;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsFilter;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SummonAction")
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SummonAction extends Validable {

    @EmbeddedId
    private SummonActionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idSummon")
    @JoinColumn(name = "idSummon")
    private Summon summon;

    @Column
    private Integer range;

    //region Validation
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Range must be greater than 0.
        if (range <= 0) {
            errors.add(new ValidationError("SummonAction", "range", range, "Range must be greater than 0."));
        }

        // Summon can't have the same action as the one that summoned it.
        // TODO this needs to be more complex
        if (Objects.equals(summon.getAction().getId(), id.getIdAction())) {
            errors.add(new ValidationError("SummonAction", "id", id, "Summon can't have the same action as the one that summoned it."));
        }

        // Summon itself must be validated.
        validateChild(summon, validationConfig);
    }

    @Override
    public String getValidableValue() {
        return summon.getTitle() + " for " + range + " hexes.";
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummonActionId implements Serializable {
        @Column(name = "idSummon")
        private Integer idSummon;

        @Column(name = "idAction")
        private Integer idAction;
    }
    //endregion
}
