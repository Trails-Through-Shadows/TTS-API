package cz.trailsthroughshadows.api.table.effect;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SkillEffect")
public class SkillEffect {

    @Id
    @Column
    private int idSkill;

    @Id
    @Column
    private int idEffect;

    @ManyToOne
    @JoinColumn(name = "idEffect", insertable = false, updatable = false)
    private Effect effect;

}