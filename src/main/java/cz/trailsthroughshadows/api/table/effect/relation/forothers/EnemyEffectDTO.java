package cz.trailsthroughshadows.api.table.effect.relation.forothers;

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
@Table(name = "EnemyEffect")
public class EnemyEffectDTO {

    @Id
    @Column(nullable = false)
    private int idEnemy;

    @Id
    @Column(nullable = false)
    private int idEffect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idEffect", insertable = false, updatable = false)
    private EffectDTO effect;

}
