package cz.trailsthroughshadows.api.table.schematic.hex.model.dto;

import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Hex")
@EqualsAndHashCode(callSuper = true)
public class HexDTO extends Validable {

    @EmbeddedId
    private HexId key;

    @Column(name = "qCoord", nullable = false)
    private int q;

    @Column(name = "rCoord", nullable = false)
    private int r;

    @Column(name = "sCoord", nullable = false)
    private int s;

    @Data
    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HexId implements Serializable {

        @Column(name = "idPart", nullable = false)
        private Integer idPart;

        @Column(name = "id", nullable = false)
        private Integer id;

    }

    //region Validation
    @Override
    public void validateInner(ValidationConfig validationConfig) {
        // hex has to have correct coordinates
        if (getQ() + getR() + getS() != 0) {
            errors.add("Hex %s has to have correct coordinates!".formatted(getIdentifier()));
        }
    }

    @Override
    public String getIdentifier() {
        return "(%d, %d, %d)".formatted(getQ(), getR(), getS());
    }
    //endregion
}
