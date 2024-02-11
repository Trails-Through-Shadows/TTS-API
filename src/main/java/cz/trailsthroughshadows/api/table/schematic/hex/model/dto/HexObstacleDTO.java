package cz.trailsthroughshadows.api.table.schematic.hex.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsFilter;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.ObstacleDTO;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "HexObstacle")
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
public class HexObstacleDTO {

    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private HexObstacleId key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idObstacle", insertable = false, updatable = false)
    private ObstacleDTO obstacle;

    @Getter
    @Embeddable
    public static class HexObstacleId implements Serializable {

        @Column(nullable = false)
        private Integer idObstacle;

        @Column(nullable = false)
        private Integer idHex;

        @Column(nullable = false)
        private Integer idPart;

        @Column(nullable = false)
        private Integer idLocation;
    }
}
