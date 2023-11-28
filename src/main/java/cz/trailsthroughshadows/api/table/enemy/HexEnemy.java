package cz.trailsthroughshadows.api.table.enemy;

import cz.trailsthroughshadows.api.table.hex.Hex;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "HexEnemy")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HexEnemy {
    @Id
    private HexEnemyId id;

    @Column(nullable = false)
    private int amount;

    @Embeddable
    public class HexEnemyId implements Serializable {
        @Column(nullable = false)
        private Hex.HexId idHex;

        @Column(nullable = false)
        private int idEnemy;
    }
}

