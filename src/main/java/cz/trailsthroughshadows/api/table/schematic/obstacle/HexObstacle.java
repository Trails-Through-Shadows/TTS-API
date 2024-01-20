package cz.trailsthroughshadows.api.table.schematic.obstacle;

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

    @ManyToOne
    @JoinColumn(name = "idObstacle", insertable = false, updatable = false)
    private Obstacle obstacle;

    @Embeddable
    public static class HexObstacleId implements Serializable {

        @Column(nullable = false)
        private int idObstacle;
        @Column(nullable = false)
        private Integer idHex;
        @Column(nullable = false)
        private Integer idPart;
        @Column(nullable = false)
        private Integer idLocation;
    }
}
