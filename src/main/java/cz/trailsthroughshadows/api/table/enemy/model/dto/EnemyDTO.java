package cz.trailsthroughshadows.api.table.enemy.model.dto;

import cz.trailsthroughshadows.api.table.action.Action;
import cz.trailsthroughshadows.api.table.effect.Effect;
import cz.trailsthroughshadows.api.table.effect.forothers.EnemyEffect;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Enemy")
public class EnemyDTO implements Cloneable {

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
    private List<EnemyActionDTO> actions;

    public List<Effect> getEffects() {
        if (effects == null) return new ArrayList<>();
        return effects.stream().map(EnemyEffect::getEffect).toList();
    }

    public List<Action> getActions() {
        if (actions == null) return new ArrayList<>();
        return actions.stream().map(EnemyActionDTO::getAction).toList();
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
