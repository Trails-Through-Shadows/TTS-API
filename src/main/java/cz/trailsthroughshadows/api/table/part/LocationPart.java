package cz.trailsthroughshadows.api.table.part;

import cz.trailsthroughshadows.api.table.location.Location;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;

import java.io.Serializable;

@Entity
@Table(name = "LocationPart")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationPart {

    @EmbeddedId
    @Setter(AccessLevel.NONE)
    private LocationPartId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idLocation", insertable = false, updatable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idPart", insertable = false, updatable = false)
    private Part part;

    @Embeddable
    public class LocationPartId implements Serializable {
        @Column(nullable = false)
        private int idLocation;

        @Column(nullable = false)
        private int idPart;
    }
}

