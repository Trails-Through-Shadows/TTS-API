package cz.trailsthroughshadows.api.table.market.market;

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
    private Integer defAmount;

    @Column
    private Integer defPrice;

    // TODO getter for items in this market

    @Embeddable
    @Data
    public static class MarketId implements Serializable {
        @Column
        private Integer idItem;
        @Column
        private Integer idLocation;
    }
}
