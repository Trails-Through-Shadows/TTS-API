package cz.trailsthroughshadows.api.table.enemy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.api.table.effect.forothers.EnemyEffect;
import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import cz.trailsthroughshadows.api.table.schematic.location.Location;
import cz.trailsthroughshadows.api.table.schematic.part.Part;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.NotImplementedException;

import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "Enemy")
@Data
@NoArgsConstructor
public class Enemy extends cz.trailsthroughshadows.algorithm.entity.Entity implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = false)
    private int health;

    @Column(nullable = false)
    private int defence;

    @OneToMany(mappedBy = "idEnemy", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Collection<EnemyEffect> effects;

    @OneToMany(mappedBy = "idEnemy", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Collection<EnemyAction> actions;


    @OneToMany(mappedBy = "idEnemy", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Collection<HexEnemy> position;

    //TODO HexEnemy
    // oh nononononono to bude booolet namapovat ten HexEnemy na ten konkrétní hexagon

    //    EnemyLocation GetHex() {
    //        throw new NotImplementedException("UNDER CONSTRUCTION\n Returns hexagon with part and location");
    //    }
    //
    //
    //    @Data
    //    @NoArgsConstructor
    //    @AllArgsConstructor
    //    public class EnemyLocation implements Serializable {
    //        Hex hex;
    //        Part part;
    //        Location location;
    //    }

    @Override
    public Enemy clone() {
        Enemy enemy = new Enemy();

        enemy.setId(this.getId());
        enemy.setName(this.getName());
        enemy.setHealth(this.getHealth());
        enemy.setDefence(this.getDefence());

        return enemy;
    }
}
