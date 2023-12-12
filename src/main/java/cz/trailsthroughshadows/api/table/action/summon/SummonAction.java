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

    @Transient
    private Summon summon;

    @Transient
    private Action action;


    @Column
    private Integer range;

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class SummonActionId implements Serializable {
        //@Column(name = "idSummon")
        @ManyToOne()
        private Summon summon;
        //@Column(name = "idAction")
        @ManyToOne()
        private Action action;
    }
}

