package cz.trailsthroughshadows.api.table.enemy;

import cz.trailsthroughshadows.api.table.action.Action;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "EnemyAction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnemyAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int id;

    @Column(name = "levelReq", nullable = false)
    private int levelReq;

    @Column(name = "idEnemy", nullable = false)
    private int idEnemy;

    @Column(name = "idAction", nullable = false)
    private int idAction;

    // Assuming there's an Action entity
    @ManyToOne
    @JoinColumn(name = "idAction", referencedColumnName = "id", insertable = false, updatable = false)
    private Action action;
}
