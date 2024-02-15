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
@Table(name = "RaceEffect")
public class RaceEffect {

    @EmbeddedId
    private RaceEffectId key;

    @Column
    private Integer levelReq;

    @ManyToOne
    @JoinColumn(name = "idEffect", insertable = false, updatable = false)
    private EffectDTO effect;

    @Embeddable
    @Data
    public static class RaceEffectId implements Serializable {
        @Column(nullable = false)
        private int idRace;

        @Column(nullable = false)
        private int idEffect;
    }
}