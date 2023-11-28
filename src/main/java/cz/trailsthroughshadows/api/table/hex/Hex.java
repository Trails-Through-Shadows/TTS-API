package cz.trailsthroughshadows.api.table.hex;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Hex")
public class Hex {

    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private HexKey key;

    private int xCord;
    private int yCord;

    @Embeddable
    public class HexKey implements Serializable {

        @Column(name = "idPart", nullable = false) // TODO  mapping to idPart but it is also id
        private int idPart;

        @Column(name = "id", nullable = false)
        private int id;

    }


}
