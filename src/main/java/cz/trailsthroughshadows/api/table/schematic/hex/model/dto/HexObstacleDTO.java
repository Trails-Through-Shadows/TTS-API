package cz.trailsthroughshadows.api.table.schematic.hex.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.ObstacleDTO;
import jakarta.annotation.Nullable;
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
public class HexObstacleDTO extends Validable {

    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private HexObstacleId key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idObstacle", insertable = false, updatable = false)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private ObstacleDTO obstacle;
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        if (key == null) {
            errors.add(new cz.trailsthroughshadows.api.rest.model.error.type.ValidationError("HexObstacle", "key", null, "Key must not be null."));
        }
        if (!errors.isEmpty()) { return; }
        if (key.idObstacle == null) {
            errors.add(new cz.trailsthroughshadows.api.rest.model.error.type.ValidationError("HexObstacle", "key.idObstacle", null, "Obstacle ID must not be null."));
        }
        if (key.idHex == null) {
            errors.add(new cz.trailsthroughshadows.api.rest.model.error.type.ValidationError("HexObstacle", "key.idHex", null, "Hex ID must not be null."));
        }
        if (key.idLocation == null) {
            errors.add(new cz.trailsthroughshadows.api.rest.model.error.type.ValidationError("HexObstacle", "key.idLocation", null, "Location ID must not be null."));
        }
        if (key.idPart == null) {
            errors.add(new cz.trailsthroughshadows.api.rest.model.error.type.ValidationError("HexObstacle", "key.idPart", null, "Part ID must not be null."));
        }
        if (obstacle == null) {
            errors.add(new cz.trailsthroughshadows.api.rest.model.error.type.ValidationError("HexObstacle", "obstacle", null, "Obstacle must not be null."));
        }
        validateChild(obstacle, validationConfig);
        if (!errors.isEmpty()) { return; }
        if (obstacle.getId() == null) {
            errors.add(new cz.trailsthroughshadows.api.rest.model.error.type.ValidationError("HexObstacle", "obstacle.id", null, "Obstacle ID must not be null."));
        }
    }

    @Override
    public String getValidableValue() {
        return null;
    }

    @Data
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
