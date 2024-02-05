package cz.trailsthroughshadows.api.table.enemy.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.trailsthroughshadows.api.table.action.Action;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EnemyAction")
public class EnemyActionDTO {
    @Id
    @Column(nullable = false)
    @JsonIgnore
    private int idEnemy;

    @Id
    @Column(nullable = false)
    @JsonIgnore
    private int idAction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idAction", insertable = false, updatable = false)
    private Action action;

}
