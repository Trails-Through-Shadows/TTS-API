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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEnemy", referencedColumnName = "id", insertable = false, updatable = false)
    private Enemy enemy;

    // Assuming there's an Action entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idAction", referencedColumnName = "id", insertable = false, updatable = false)
    private Action action;
}
