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
    @MapsId("idSummon")
    @JoinColumn(name = "idSummon")
    private Summon summon;

    @ManyToOne
    @MapsId("idAction")
    @JoinColumn(name = "idAction")
    private Action action;

    @Column
    private Integer range;

    @Embeddable
    class SummonActionId implements Serializable {
        private Integer idSummon;
        private Integer idAction;
    }

}
