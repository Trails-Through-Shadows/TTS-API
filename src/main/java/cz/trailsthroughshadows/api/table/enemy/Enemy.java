package cz.trailsthroughshadows.api.table.enemy;

import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
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

    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = false)
    private int health;

    @Column(nullable = false)
    private int defence;

    @Transient
    private Hex hex;
}
