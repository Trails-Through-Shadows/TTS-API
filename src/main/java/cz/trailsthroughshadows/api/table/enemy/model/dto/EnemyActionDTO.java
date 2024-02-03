package cz.trailsthroughshadows.api.table.enemy.model.dto;

import cz.trailsthroughshadows.api.table.action.Action;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Entity
@Table(name = "EnemyAction")
@NoArgsConstructor
@AllArgsConstructor
public class EnemyActionDTO {

    @EmbeddedId
    private EnemyActionId key;

    @ManyToOne
    @JoinColumn(name = "idAction", referencedColumnName = "id", insertable = false, updatable = false)
    private Action action;

    @Embeddable
    @Data
    public static class EnemyActionId implements Serializable {

        @Column(nullable = false)
        private int idEnemy;

        @Column(nullable = false)
        private int idAction;

    }
}
