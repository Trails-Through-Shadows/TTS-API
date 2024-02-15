package cz.trailsthroughshadows.api.table.effect.relation.forcharacter;

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
@Table(name = "ClassEffect")
public class ClazzEffect {

    @EmbeddedId
    private ClazzEffectId key;

    @Column
    private Integer levelReq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEffect", insertable = false, updatable = false)
    private EffectDTO effect;

    @Embeddable
    @Data
    public static class ClazzEffectId implements Serializable {
        @Column(nullable = false)
        private Integer idClass;

        @Column(nullable = false)
        private Integer idEffect;
    }

}