package cz.trailsthroughshadows.api.table.effect.relation.forcharacter;

import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ClassEffect")
public class ClazzEffect {

    @Id
    @Column(nullable = false)
    private int idClass;

    @Id
    @Column(nullable = false)
    private int idEffect;

    @Column
    private int levelReq;

    @ManyToOne
    @JoinColumn(name = "idEffect", insertable = false, updatable = false)
    private EffectDTO effect;
}