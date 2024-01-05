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

    @Column(name = "qCord", nullable = false)
    private int q;

    @Column(name = "rCord", nullable = false)
    private int r;

    @Column(name = "sCord", nullable = false)
    private int s;

    @Data
    @Embeddable
    public static class HexId implements Serializable {

        @Column(name = "idPart", nullable = false)
        private int idPart;

        @Column(name = "id", nullable = false)
        private int id;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hex hex)) return false;
        return q == hex.q && r == hex.r && s == hex.s && key.equals(hex.key);
    }
}
