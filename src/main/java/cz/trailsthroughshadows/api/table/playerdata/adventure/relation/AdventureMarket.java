package cz.trailsthroughshadows.api.table.playerdata.adventure.relation;

import cz.trailsthroughshadows.api.table.market.item.model.ItemDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`AdventureMarket`")
@Entity
public class AdventureMarket {

    @EmbeddedId
    private AdventureMarketId key;

    @Column
    private Integer price;

    @Column
    private Integer amount;

    @ManyToOne
    @JoinColumn(name = "idItem")
    private ItemDTO item;


    @Embeddable
    @Data
    @NoArgsConstructor
    public static class AdventureMarketId implements Serializable {
        @Column(insertable = false, updatable = false, nullable = false)
        private Integer idLocation;

        @Column(insertable = false, updatable = false, nullable = false)
        private Integer idItem;

        @Column(insertable = false, updatable = false, nullable = false)
        private Integer idAdventure;
    }
}
