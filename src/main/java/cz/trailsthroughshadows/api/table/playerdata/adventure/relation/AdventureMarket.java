package cz.trailsthroughshadows.api.table.playerdata.adventure.relation;

import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.table.market.item.model.ItemDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`AdventureMarket`")
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class AdventureMarket {

    @EmbeddedId
    private AdventureMarketId key;

    @Column
    private Integer price;

    @Column
    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
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
