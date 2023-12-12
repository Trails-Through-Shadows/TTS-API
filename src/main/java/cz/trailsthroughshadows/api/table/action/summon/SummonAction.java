package cz.trailsthroughshadows.api.table.action.summon;


import cz.trailsthroughshadows.api.table.action.Action;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.function.IntBinaryOperator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SummonAction")
public class SummonAction {

    @EmbeddedId
    private SummonActionId id;

    @ManyToOne()
    @JoinColumn(name = "idSummon")
    private Summon summon;

    @ManyToOne()
    @MapKey(name = "idAction")
    @JoinColumn(name = "idAction")
    private Action action;


    @Column
    private Integer range;

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class SummonActionId implements Serializable {
        @Column()
        private Integer idSummon;
        @Column()
        private Integer idAction;
    }
}

