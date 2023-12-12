package cz.trailsthroughshadows.api.table.action.summon;

import cz.trailsthroughshadows.api.table.effect.Effect;
import cz.trailsthroughshadows.api.table.effect.SummonEffect;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Summon")
public class Summon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)//zrušení jen setteru
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column
    private Integer duration;

    @Column
    private Integer health;

    @Column
    private Integer idAction;  //todo foreign key

    @OneToMany(mappedBy = "pk.summon")
    private Collection<SummonAction> actions;

    @OneToMany(mappedBy = "idSummon", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Collection<SummonEffect> effects;

    // Skipping n-to-n relationship, there is no additional data in that table
    @ToString.Include(name = "effects") // Including replacement field in toString
    public Collection<Effect> getEffects() {
        if (effects == null) return null;
        return effects.stream().map(SummonEffect::getEffect).toList();
    }

}
