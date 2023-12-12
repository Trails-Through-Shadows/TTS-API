package cz.trailsthroughshadows.api.table.action.attack;


import cz.trailsthroughshadows.api.table.effect.AttackEffect;
import cz.trailsthroughshadows.api.table.effect.Effect;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity
@Table(name = "Attack")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(nullable = false)
    private int range;

    @Column(nullable = false)
    private int damage;

    @Column
    private Integer area;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Effect.EffectTarget target;

    @Column(nullable = false)
    private int numAttacks;

    @OneToMany(mappedBy = "idAttack", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Collection<AttackEffect> effects;


    // Skipping n-to-n relationship, there is no additional data in that table
    @ToString.Include(name = "effects") // Including replacement field in toString
    public Collection<Effect> getEffects() {
        if (effects == null) return null;
        return effects.stream().map(AttackEffect::getEffect).toList();
    }

}
