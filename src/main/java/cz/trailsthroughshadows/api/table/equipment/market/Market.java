package cz.trailsthroughshadows.api.table.equipment.market;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "Market")
@Data
@NoArgsConstructor
public class Market {

    @EmbeddedId
    private MarketId key;

    @Column(nullable = false)
    private Boolean unlocked;

    @Column(nullable = false)
    private Integer amount;

    // TODO getter for items in this market

    @Embeddable
    public static class MarketId implements Serializable {
        @Column
        private Integer idItem;
        @Column
        private Integer idAdventure;
    }
}
