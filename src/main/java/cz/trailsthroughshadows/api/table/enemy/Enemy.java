package cz.trailsthroughshadows.api.table.enemy;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Enemy")
@Data
@NoArgsConstructor
public class Enemy extends cz.trailsthroughshadows.algorithm.entity.Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = false)
    private int health;

    @Column(nullable = false)
    private int defence;
}
