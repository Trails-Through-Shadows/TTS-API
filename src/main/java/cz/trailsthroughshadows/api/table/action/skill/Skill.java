package cz.trailsthroughshadows.api.table.action.skill;

import cz.trailsthroughshadows.api.table.effect.Effect;
import cz.trailsthroughshadows.api.table.effect.SkillEffect;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity
@Table(name = "Skill")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(nullable = false)
    private int range;

    @Column
    private Integer area;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Effect.EffectRange target;

    @OneToMany(mappedBy = "idSkill", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Collection<SkillEffect> effects;


    // Skipping n-to-n relationship, there is no additional data in that table
    @ToString.Include(name = "effects") // Including replacement field in toString
    public Collection<Effect> getEffects() {
        if (effects == null) return null;
        return effects.stream().map(SkillEffect::getEffect).toList();
    }

}
