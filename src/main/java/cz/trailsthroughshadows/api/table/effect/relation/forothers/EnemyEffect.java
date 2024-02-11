package cz.trailsthroughshadows.api.table.effect.relation.forothers;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsFilter;
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
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
public class EnemyEffect {
    @Id
    @Column(nullable = false)
    private int idEnemy;

    @Id
    @Column(nullable = false)
    private int idEffect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEffect", insertable = false, updatable = false)
    private EffectDTO effect;
}
