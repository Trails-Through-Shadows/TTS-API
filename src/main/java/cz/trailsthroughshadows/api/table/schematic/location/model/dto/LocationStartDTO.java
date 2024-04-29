package cz.trailsthroughshadows.api.table.schematic.location.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexDTO;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "LocationStart")
@IdClass(LocationStartDTO.class)
public class LocationStartDTO extends Validable implements Serializable {

    @Id
    private Integer idLocation;

    @Id
    private Integer idPart;

    @Id
    private Integer idHex;

    @Transient
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idHex", insertable = false, updatable = false)
    @JoinColumn(name = "idPart", insertable = false, updatable = false)
    private HexDTO hex;

    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        if (idLocation == null) {
            errors.add(new ValidationError("LocationStart", "idLocation", null, "Location ID must not be null."));
        }
        if (idPart == null) {
            errors.add(new ValidationError("LocationStart", "idPart", null, "Part ID must not be null."));
        }
        if (idHex == null && hex == null) {
            errors.add(new ValidationError("LocationStart", "idHex", null, "Hex ID must not be null."));
        }
        validateChild(hex, validationConfig);
    }

    @Override
    public String getValidableValue() {
        return null;
    }
}

