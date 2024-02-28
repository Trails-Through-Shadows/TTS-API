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

    @Column(nullable = false)
    private Integer qCoord;

    @Column(nullable = false)
    private Integer rCoord;

    @Column(nullable = false)
    private Integer sCoord;

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