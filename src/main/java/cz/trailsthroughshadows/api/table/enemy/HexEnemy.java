package cz.trailsthroughshadows.api.table.enemy;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "HexEnemy")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HexEnemy {

    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private HexEnemyId key;

    @Embeddable
    @Data
    public static class HexEnemyId implements Serializable {
        @Column(nullable = false)
        private int idHex;

        @Column(nullable = false)
        private int idLocation;

        @Column(nullable = false)
        private int idPart;
    }
}

