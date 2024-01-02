package cz.trailsthroughshadows.api.table.effect.forothers;

import cz.trailsthroughshadows.api.table.effect.Effect;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ItemEffect")
public class ItemEffect {
    @Id
    @Column(nullable = false)
    private int idItem;

    @Id
    @Column(nullable = false)
    private int idEffect;

    @ManyToOne
    @JoinColumn(name = "idEffect", insertable = false, updatable = false)
    private Effect effect;
}