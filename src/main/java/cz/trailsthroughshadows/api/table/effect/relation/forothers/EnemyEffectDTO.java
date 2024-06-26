package cz.trailsthroughshadows.api.table.effect.relation.forothers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EnemyEffect")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EnemyEffectDTO {

    @EmbeddedId
    private EnemyEffect key;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idEffect", insertable = false, updatable = false)
    private EffectDTO effect;

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EnemyEffect implements Serializable {

        @Column(nullable = false)
        private Integer idEnemy;

        @Column(nullable = false)
        private Integer idEffect;
    }
}
