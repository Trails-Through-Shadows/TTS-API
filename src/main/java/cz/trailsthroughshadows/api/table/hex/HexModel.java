package cz.trailsthroughshadows.api.table.hex;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Hex")
public class HexModel {

    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)//zručení jen setteru
    private HexKey key;


    @Embeddable
    public class HexKey implements Serializable {

        @Column(name = "idPart", nullable = false)
        private int idPart;

        @Column(name = "id", nullable = false)
        private int id;

        /** getters and setters **/
    }


}
