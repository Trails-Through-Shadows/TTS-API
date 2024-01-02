package cz.trailsthroughshadows.api.table.action.summon;

import cz.trailsthroughshadows.api.table.action.Action;
import cz.trailsthroughshadows.api.table.effect.Effect;
import cz.trailsthroughshadows.api.table.effect.foraction.SummonEffect;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Summon")
public class Summon extends cz.trailsthroughshadows.algorithm.entity.Entity implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column
    private Integer duration;

    @Column
    private Integer health;

    @ManyToOne()
    @JoinColumn(name = "idAction")
    private Action action;

//    @OneToMany(mappedBy = "summon")
//    private Collection<SummonAction> actions;

    @OneToMany(mappedBy = "idSummon")
    @ToString.Exclude
    private Collection<SummonEffect> effects;

    // Skipping n-to-n relationship, there is no additional data in that table
    @ToString.Include(name = "effects") // Including replacement field in toString
    public Collection<Effect> getEffects() {
        if (effects == null) return null;
        return effects.stream().map(SummonEffect::getEffect).toList();
    }

    public Collection<SummonEffect> getRawEffects() {
        return effects;
    }

    @Override
    public Summon clone() {
        Summon summon = new Summon();

        summon.setId(this.getId());
        summon.setName(this.getName());
        summon.setDuration(this.getDuration());
        summon.setHealth(this.getHealth());
        summon.setAction(this.getAction());
        summon.setEffects(this.getRawEffects());

        return summon;
    }

    @Override
    public List<Action> getActions() {
        return List.of(action);
    }
}
