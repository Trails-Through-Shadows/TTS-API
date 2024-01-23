package cz.trailsthroughshadows.api.table.action.skill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.trailsthroughshadows.api.table.effect.Effect;
import cz.trailsthroughshadows.api.table.effect.foraction.SkillEffect;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity
@Table(name = "Skill")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    private Effect.EffectTarget target;

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
