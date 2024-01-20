package cz.trailsthroughshadows.api.table.schematic.hex;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LocationDoor")
public class LocationDoor {

    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private PartDoorId key;


    @Data
    @Embeddable
    public static class PartDoorId implements Serializable {
        @Column(nullable = false)
        private int location;

        @Column(nullable = false)
        private int fromPart;

        @Column(nullable = false)
        private int toPart;

        @Column(nullable = false)
        private int hex;
    }

}
