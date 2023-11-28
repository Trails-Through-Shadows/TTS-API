package cz.trailsthroughshadows.api.table.enemy;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Enemy")
@Data
@NoArgsConstructor
public class Enemy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "health", nullable = false)
    private int health;

    @Column(name = "defence", nullable = false)
    private int defence;

    // Additional attributes and relationships if any
}
