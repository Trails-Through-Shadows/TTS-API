package cz.trailsthroughshadows.api.table.schematic.part;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "LocationPart")
public class LocationPart {

    @EmbeddedId
    private LocationPartId key;

    @ManyToOne
    @JoinColumn(name = "idPart", insertable = false, updatable = false)
    private Part part;

    @Column(nullable = false)
    private int rotation;

    @Embeddable
    @Data
    public static class LocationPartId implements java.io.Serializable {
        @Column(name = "idLocation")
        private int idLocation;

        @Column(name = "idPart")
        private int idPart;
    }

}
