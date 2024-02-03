package cz.trailsthroughshadows.api.table.schematic.hex;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Hex")
public class Hex {

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
}
