package cz.trailsthroughshadows.api.table.action;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.api.rest.jsonfilter.LazyFieldsFilter;
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
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movement")
    private Movement movement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill")
    private Skill skill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attack")
    private Attack attack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restoreCards")
    private RestoreCards restoreCards;

    @OneToMany(mappedBy = "idAction", fetch = FetchType.LAZY)
    private Collection<SummonAction> summonActions;

    public enum Discard implements Serializable {
        PERMANENT,
        SHORT_REST,
        LONG_REST,
        NEVER
    }
}
