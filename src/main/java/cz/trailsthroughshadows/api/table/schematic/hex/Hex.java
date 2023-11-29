package cz.trailsthroughshadows.api.table.schematic.hex;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Hex")
public class Hex {

    @EmbeddedId
    private HexId key;

    @Column(name = "xCord")
    private int xCord;

    @Column(name = "yCord")
    private int yCord;

    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HexId implements Serializable {

        @Column(name = "idPart", nullable = false)
        private int idPart;

        @Column(name = "id", nullable = false)
        private int id;

    }
}
