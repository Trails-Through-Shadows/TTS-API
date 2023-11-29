package cz.trailsthroughshadows.api.table.schematic.part;

import cz.trailsthroughshadows.api.table.schematic.location.Location;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "LocationPart")
public class LocationPart {

    @EmbeddedId
    @Setter(AccessLevel.NONE)
    private LocationPartId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "Part", joinColumns = @JoinColumn(name = "idPart"))
    private Location location;

    @Embeddable
    @Data
    public static class LocationPartId implements Serializable {
        @Column(nullable = false)
        private int idLocation;

        @Column(nullable = false)
        private int idPart;
    }
}

