package cz.trailsthroughshadows.api.table.action;

import cz.trailsthroughshadows.api.table.action.attack.Attack;
import cz.trailsthroughshadows.api.table.action.movement.Movement;
import cz.trailsthroughshadows.api.table.action.restorecards.RestoreCards;
import cz.trailsthroughshadows.api.table.action.skill.Skill;
import cz.trailsthroughshadows.api.table.action.summon.SummonAction;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "Action")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Action {
    @Transient
    Boolean discarded = false;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int id;
    @Column(nullable = false, length = 50)
    private String title;
    @Column
    private String description;
    @Column
    @Enumerated(EnumType.STRING)
    private Discard discard;
    @Column
    private Integer levelReq;
    @ManyToOne()
    @JoinColumn(name = "movement")
    private Movement movement;
    @ManyToOne()
    @JoinColumn(name = "skill")
    private Skill skill;
    @ManyToOne()
    @JoinColumn(name = "attack")
    private Attack attack;
    @ManyToOne()
    @JoinColumn(name = "restoreCards")
    private RestoreCards restoreCards;
    @OneToMany(mappedBy = "idAction")
    private Collection<SummonAction> summonActions;

    public enum Discard implements Serializable {
        PERMANENT,
        SHORT_REST,
        LONG_REST,
        NEVER
    }
}
