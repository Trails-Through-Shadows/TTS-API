package cz.trailsthroughshadows.api.table.action.summon;


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

    @Column(name = "idAction", insertable = false, updatable = false)
    private int idAction;

    @Column
    private Integer range;

    @Embeddable
    public static class SummonActionId implements Serializable {
        @Column(name = "idSummon")
        private Integer summon_id;
        @Column(name = "idAction")
        private Integer action_id;
    }
}

