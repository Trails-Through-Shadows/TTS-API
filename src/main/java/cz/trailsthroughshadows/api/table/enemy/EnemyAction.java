package cz.trailsthroughshadows.api.table.enemy;

import cz.trailsthroughshadows.api.table.action.Action;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "EnemyAction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnemyAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "idEnemy", nullable = false)
    private int idEnemy;

    @Column(name = "levelReq", nullable = false)
    private int levelReq;

    @Column(name = "idAction", nullable = false)
    private int idAction;

    @ManyToOne
    @JoinColumn(name = "idEnemy", referencedColumnName = "id", insertable = false, updatable = false)
    private Enemy enemy;

    // Assuming there's an Action entity
    @ManyToOne
    @JoinColumn(name = "idAction", referencedColumnName = "id", insertable = false, updatable = false)
    private Action action;
}
