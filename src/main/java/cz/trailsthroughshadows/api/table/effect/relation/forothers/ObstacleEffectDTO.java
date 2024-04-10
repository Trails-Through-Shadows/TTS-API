package cz.trailsthroughshadows.api.table.effect.relation.forothers;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ObstacleEffect")
public class ObstacleEffectDTO {

    @EmbeddedId
    private ObstacleEffectKey key;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idEffect", insertable = false, updatable = false)
    private EffectDTO effect;

    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ObstacleEffectKey implements Serializable {
        @Column(nullable = false)
        private Integer idObstacle;

        @Column(nullable = false)
        private Integer idEffect;
    }
}