package cz.trailsthroughshadows.api.table.enemy;

import cz.trailsthroughshadows.api.table.action.Action;
import cz.trailsthroughshadows.api.table.effect.Effect;
import cz.trailsthroughshadows.api.table.effect.forothers.EnemyEffect;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Enemy")
public class Enemy extends cz.trailsthroughshadows.algorithm.entity.Entity implements Cloneable {

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
    @ToString.Exclude
    private Collection<EnemyEffect> effects;
    @OneToMany(mappedBy = "key.idEnemy", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Collection<EnemyAction> actions;

    @ToString.Include(name = "effects")
    public Collection<Effect> getEffects() {
        if (effects == null) return null;
        return effects.stream().map(EnemyEffect::getEffect).toList();
    }

    @ToString.Include(name = "actions")
    public Collection<Action> getActions() {
        if (actions == null) return null;
        return actions.stream().map(EnemyAction::getAction).toList();
    }


    @Override
    public Enemy clone() {
        Enemy enemy = new Enemy();

        enemy.setId(this.getId());
        enemy.setName(this.getName());
        enemy.setBaseDefence(this.getBaseDefence());
        enemy.setBaseHealth(this.getBaseHealth());

        return enemy;
    }
}
