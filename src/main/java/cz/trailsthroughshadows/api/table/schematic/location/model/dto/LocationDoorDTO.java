package cz.trailsthroughshadows.api.table.schematic.location.model.dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "LocationDoor")
public class LocationDoorDTO implements Serializable {

    @EmbeddedId
    private LocationDoorId key;

    @Column(nullable = false, name = "qCoord")
    private Integer q;

    @Column(nullable = false, name = "rCoord")
    private Integer r;

    @Column(nullable = false, name = "sCoord")
    private Integer s;

    @Data
    @Embeddable
    public static class LocationDoorId implements Serializable {
        @Column(nullable = false)
        private int idLocation;
        @Column(nullable = false)
        private int idPartFrom;
        @Column(nullable = false)
        private int idPartTo;
    }

}