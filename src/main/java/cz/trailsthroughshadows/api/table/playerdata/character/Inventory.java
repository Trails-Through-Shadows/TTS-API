package cz.trailsthroughshadows.api.table.playerdata.character;

import cz.trailsthroughshadows.api.table.market.item.Item;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "Inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @EmbeddedId
    @Setter(AccessLevel.NONE)
    private InventoryId id;  // Assuming composite key

    @ManyToOne
    @JoinColumn(name = "idItem", insertable = false, updatable = false)
    private Item item;

    @Column(nullable = false)
    private int amount;

    @Embeddable
    @Data
    public static class InventoryId implements Serializable {
        @Column(nullable = false)
        private int idCharacter;

        @Column(nullable = false)
        private int idItem;
    }
}

