package cz.trailsthroughshadows.api.table.schematic.location.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.schematic.part.model.PartDTO;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "LocationPart")
public class LocationPartDTO  extends Validable {

    @EmbeddedId
    private LocationPartId key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idPart", insertable = false, updatable = false)
    private PartDTO part;

    @Column(nullable = false)
    private int rotation;

    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        if (part == null) {
            errors.add(new ValidationError("LocationPart", "part", null, "Part must not be null."));
        }
        validateChild(part, validationConfig);
        if (!errors.isEmpty()) { return; }
        if (part.getId() == null) {
            errors.add(new ValidationError("LocationPart", "part.id", null, "Part ID must not be null."));
        }
        if (key == null) {
            errors.add(new ValidationError("LocationPart", "key", null, "Key must not be null."));
        }
        if (!errors.isEmpty()) { return; }
        if (key.getIdLocation() == null) {
            errors.add(new ValidationError("LocationPart", "key.idLocation", null, "Location ID must not be null."));
        }
        if (key.getIdPart() == null) {
            errors.add(new ValidationError("LocationPart", "key.idPart", null, "Part ID must not be null."));
        }
        if (rotation < 0 || rotation > 5) {
            errors.add(new ValidationError("LocationPart", "rotation", rotation, "Rotation must be between 0 and 5."));
        }
    }

    @Override
    public String getValidableValue() {
        return null;
    }

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocationPartId implements Serializable {

        @Column(name = "idLocation")
        private Integer idLocation;

        @Column(name = "idPart")
        private Integer idPart;

    }
}
