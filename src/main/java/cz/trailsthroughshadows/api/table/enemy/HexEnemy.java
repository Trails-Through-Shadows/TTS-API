package cz.trailsthroughshadows.api.table.enemy;

import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "HexEnemy")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HexEnemy {

    @Column(nullable = false)
    private int idEnemy;

    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private HexEnemyId key;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "idHex", insertable = false, updatable = false),
            @JoinColumn(name = "idPart", insertable = false, updatable = false)
    })
    private Hex hex;


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

