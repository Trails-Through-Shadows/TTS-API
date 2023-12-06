package cz.trailsthroughshadows.api.table.action.summon;


import cz.trailsthroughshadows.api.table.action.Action;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SummonAction")
public class SummonAction {

    @EmbeddedId
    private SummonActionId id;

    @ManyToOne
    @MapsId("summon_id")
    @JoinColumn(name = "idSummon")
    private Summon summon;

    @ManyToOne
    @MapsId("action_id")
    @JoinColumn(name = "idAction")
    private Action action;

    @Column
    private Integer range;

    //todo jebat tohle je zacyklené jak cyp
    // věci odkud jsem bral https://www.baeldung.com/jpa-many-to-many
    // třetí kapitola s multiklíčem a jednou věcí navíc

}

