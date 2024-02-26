package cz.trailsthroughshadows.api.table.schematic.hex.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class HexDTO extends Validable {

    @EmbeddedId
    private HexId key;

    @Column(name = "qCoord", nullable = false)
    private int q;

    @Column(name = "rCoord", nullable = false)
    private int r;

    @Column(name = "sCoord", nullable = false)
    private int s;

    //region Validation
    @Override
    public void validateInner(ValidationConfig validationConfig) {
        // hex has to have correct coordinates
        int sum = getQ() + getR() + getS();
        if (sum != 0) {
            errors.add(new ValidationError("Hex", "coords", sum, "Sum of coords q + r + s has to be 0!"));
        }
    }

    @Override
    public String getValidableValue() {
        return "(%d, %d, %d)".formatted(getQ(), getR(), getS());
    }
    //endregion

    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HexId implements Serializable {

        @Column(name = "idPart", nullable = false)
        private Integer idPart;

        @Column(name = "id", nullable = false)
        private Integer id;

    }
}
