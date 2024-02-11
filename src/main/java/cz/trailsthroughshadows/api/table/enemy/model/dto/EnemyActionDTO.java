package cz.trailsthroughshadows.api.table.enemy.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsFilter;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EnemyAction")
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
public class EnemyActionDTO {
    @Id
    @Column(nullable = false)
    private int idEnemy;

    @Id
    @Column(nullable = false)
    private int idAction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idAction", insertable = false, updatable = false)
    private ActionDTO action;

}
