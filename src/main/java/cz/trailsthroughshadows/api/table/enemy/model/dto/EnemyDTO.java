package cz.trailsthroughshadows.api.table.enemy.model.dto;

import cz.trailsthroughshadows.api.table.action.Action;
import cz.trailsthroughshadows.api.table.effect.Effect;
import cz.trailsthroughshadows.api.table.effect.forothers.EnemyEffect;
import cz.trailsthroughshadows.api.table.enemy.model.EnemyAction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Enemy")
@EqualsAndHashCode(callSuper = true)
public class EnemyDTO extends cz.trailsthroughshadows.algorithm.entity.Entity implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = false)
    private int baseHealth;

    @Column(nullable = false)
    private int baseDefence;

    @Column
    private Integer usages;

    @OneToMany(mappedBy = "idEnemy", fetch = FetchType.LAZY)
    private List<EnemyEffect> effects;

    @OneToMany(mappedBy = "key.idEnemy", fetch = FetchType.LAZY)
    private List<EnemyAction> actions;

    public List<Effect> getEffects() {
        if (effects == null) return new ArrayList<>();
        return effects.stream().map(EnemyEffect::getEffect).toList();
    }

    public List<Action> getActions() {
        if (actions == null) return new ArrayList<>();
        return actions.stream().map(EnemyAction::getAction).toList();
    }

    @Override
    public EnemyDTO clone() {
        EnemyDTO enemy = new EnemyDTO();

        enemy.setId(this.getId());
        enemy.setName(this.getName());
        enemy.setBaseDefence(this.getBaseDefence());
        enemy.setBaseHealth(this.getBaseHealth());

        return enemy;
    }
}