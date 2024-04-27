package cz.trailsthroughshadows.api.table.schematic.hex.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.ObstacleDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "HexObstacle")
public class HexObstacleDTO {

    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private HexObstacleId key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idObstacle", insertable = false, updatable = false)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private ObstacleDTO obstacle;

    @Data
    @Embeddable
    public static class HexObstacleId implements Serializable {

        @Column(nullable = false)
        private int idObstacle;

        @Column(nullable = false)
        private int idHex;

        @Column(nullable = false)
        private int idPart;

        @Column(nullable = false)
        private int idLocation;
    }
}
