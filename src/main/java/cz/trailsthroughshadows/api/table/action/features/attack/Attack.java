package cz.trailsthroughshadows.api.table.action.features.attack;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsFilter;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.foraction.AttackEffect;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity
@Table(name = "Attack")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    private EffectDTO.EffectTarget target;

    @Column(nullable = false)
    private int numAttacks;

    @OneToMany(mappedBy = "idAttack", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Collection<AttackEffect> effects;


    @ToString.Include(name = "effects") // Including replacement field in toString
    public Collection<EffectDTO> getEffects() {
        if (effects == null) return null;
        return effects.stream().map(AttackEffect::getEffect).toList();
    }

}
