package cz.trailsthroughshadows.api.table.effect.relation.forothers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ItemEffect")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ItemEffect {

    @EmbeddedId
    private ItemEffectKey key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idEffect", insertable = false, updatable = false)
    private EffectDTO effect;

    public static class ItemEffectKey implements java.io.Serializable {
        @Column(nullable = false)
        private Integer idItem;

        @Column(nullable = false)
        private Integer idEffect;
    }
}