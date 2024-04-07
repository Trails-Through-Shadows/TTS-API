package cz.trailsthroughshadows.api.table.effect.relation.foraction;

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
@Table(name = "MovementEffect")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MovementEffect {

    @EmbeddedId
    private MovementEffectId key;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "idEffect", insertable = false, updatable = false)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private EffectDTO effect;

    @Data
    @Embeddable
    public static class MovementEffectId implements Serializable {
        @Column(nullable = false)
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer idMovement;

        @Column(nullable = false)
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer idEffect;
    }
}