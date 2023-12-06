package cz.trailsthroughshadows.api.table.effect;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "AttackEffect")
public class AttackEffect {

    @Id
    @Column
    private int idAttack;

    @Id
    @Column
    private int idEffect;

    @ManyToOne
    @JoinColumn(name = "idEffect", insertable = false, updatable = false)
    private Effect effect;

}
