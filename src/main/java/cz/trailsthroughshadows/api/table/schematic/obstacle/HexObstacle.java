package cz.trailsthroughshadows.api.table.schematic.obstacle;

import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "HexObstacle")
public class HexObstacle {

    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private HexObstacleId key;

    @Column(nullable = false)
    private int idObstacle;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "idHex", insertable = false, updatable = false),
            @JoinColumn(name = "idPart", insertable = false, updatable = false)
    })
    private Hex hex;

    @Embeddable
    public static class HexObstacleId implements Serializable {
        @Column(nullable = false)
        private Integer idHex;
        @Column(nullable = false)
        private Integer idPart;
        @Column(nullable = false)
        private Integer idLocation;
    }
}
