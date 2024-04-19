package cz.trailsthroughshadows.api.table.playerdata.character.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.market.item.model.ItemDTO;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "Inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InventoryDTO extends Validable {

    @EmbeddedId
    @Setter(AccessLevel.NONE)
    private InventoryId key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idItem", insertable = false, updatable = false)
    private ItemDTO item;

    @Column(nullable = false)
    private int amount;

    // region Validation

    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Amount must be between 1 and max amount.
        if (amount <= 0) {
            errors.add(new ValidationError("Inventory", "amount", amount, "Amount must be positive."));
        }
        if (amount > validationConfig.getInventory().getMaxItems()) {
            errors.add(new ValidationError("Inventory", "amount", amount,
                    "Amount must be less than " + validationConfig.getInventory().getMaxItems() + "."));
        }

        // Item must be validated.
        validateChild(item, validationConfig);
    }

    @Override
    public String getValidableValue() {
        return key.getIdCharacter() + ": " + amount + "x " + item.getTitle();
    }

    // endregion

    @Embeddable
    @Data
    public static class InventoryId implements Serializable {
        @Column(nullable = false)
        private int idCharacter;

        @Column(nullable = false)
        private int idItem;
    }
}
