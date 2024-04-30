package cz.trailsthroughshadows.api.table.schematic.location.model.dto;

import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "LocationDoor")
public class LocationDoorDTO extends Validable implements Serializable {

    @EmbeddedId
    private LocationDoorId key;

    @Column(nullable = false, name = "qCoord")
    private Integer q;

    @Column(nullable = false, name = "rCoord")
    private Integer r;

    @Column(nullable = false, name = "sCoord")
    private Integer s;

    @Transient
    private boolean opened = false;

    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        if (key == null) {
            errors.add(new ValidationError("LocationDoor", "key", null, "Key must not be null."));
        }
        if (!errors.isEmpty()) { return; }
        if (key.idLocation == null) {
            errors.add(new ValidationError("LocationDoor", "key.idLocation", null, "Location ID must not be null."));
        }
        if (key.idPartFrom == null) {
            errors.add(new ValidationError("LocationDoor", "key.idPartFrom", null, "Part From ID must not be null."));
        }
        if (key.idPartTo == null) {
            errors.add(new ValidationError("LocationDoor", "key.idPartTo", null, "Part To ID must not be null."));
        }
        if (q == null) {
            errors.add(new ValidationError("LocationDoor", "q", null, "Q coordinate must not be null."));
        }
        if (r == null) {
            errors.add(new ValidationError("LocationDoor", "r", null, "R coordinate must not be null."));
        }
        if (s == null) {
            errors.add(new ValidationError("LocationDoor", "s", null, "S coordinate must not be null."));
        }
    }

    @Override
    public String getValidableValue() {
        return null;
    }

    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDoorId implements Serializable {
        @Column(nullable = false)
        private Integer idLocation;
        @Column(nullable = false)
        private Integer idPartFrom;
        @Column(nullable = false)
        private Integer idPartTo;
    }

}