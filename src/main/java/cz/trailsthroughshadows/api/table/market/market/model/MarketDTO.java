package cz.trailsthroughshadows.api.table.market.market.model;

import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "Market")
@Data
@NoArgsConstructor
public class MarketDTO extends Validable {

    @EmbeddedId
    private MarketId key;

    @Column(nullable = false)
    private Integer defAmount;

    @Column
    private Integer defPrice;

    // TODO getter for items in this market

    //region Validation

    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Price must be positive.
        if (defPrice != null && defPrice <= 0) {
            errors.add(new ValidationError("Market", "defPrice", getDefPrice(), "Price must be positive."));
        }

        // Amount must be positive.
        if (defAmount <= 0) {
            errors.add(new ValidationError("Market", "defAmount", getDefAmount(), "Amount must be positive."));
        }

        // Item must be validated.
        // TODO zozeee get item from market
    }

    @Override
    public String getValidableValue() {
        return defAmount + "x" + defPrice + " " + key.getIdItem() + " " + key.getIdLocation();
    }

    //endregion

    @Embeddable
    @Data
    public static class MarketId implements Serializable {
        @Column
        private Integer idItem;

        @Column
        private Integer idLocation;
    }
}
