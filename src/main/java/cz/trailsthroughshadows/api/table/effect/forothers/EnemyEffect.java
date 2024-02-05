package cz.trailsthroughshadows.api.table.effect.forothers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.trailsthroughshadows.api.table.effect.Effect;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EnemyEffect")
public class EnemyEffect {
    @Id
    @Column(nullable = false)
    @JsonIgnore
    private int idEnemy;

    @Id
    @Column(nullable = false)
    @JsonIgnore
    private int idEffect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEffect", insertable = false, updatable = false)
    private Effect effect;
}
