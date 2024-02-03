package cz.trailsthroughshadows.api.table.schematic.location.model.dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "LocationDoor")
public class LocationDoorDTO {

    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private PartDoorId key;

    @Data
    @Embeddable
    public static class PartDoorId implements Serializable {

        @Column(nullable = false)
        private int idLocation;

        @Column(nullable = false)
        private int idPartFrom;

        @Column(nullable = false)
        private int idPartTo;

        @Column(nullable = false)
        private int idHex;

    }
}
