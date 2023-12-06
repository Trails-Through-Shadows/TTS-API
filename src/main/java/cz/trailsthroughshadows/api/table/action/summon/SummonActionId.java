package cz.trailsthroughshadows.api.table.action.summon;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SummonActionId implements Serializable {
    @Column(name = "idSummon")
    private Integer summon_id;
    @Column(name = "idAction")
    private Integer action_id;
}
