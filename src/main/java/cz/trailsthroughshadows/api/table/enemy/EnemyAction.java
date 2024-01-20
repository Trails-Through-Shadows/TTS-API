package cz.trailsthroughshadows.api.table.enemy;

import cz.trailsthroughshadows.api.table.action.Action;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;


@Entity
@Table(name = "EnemyAction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnemyAction {
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
