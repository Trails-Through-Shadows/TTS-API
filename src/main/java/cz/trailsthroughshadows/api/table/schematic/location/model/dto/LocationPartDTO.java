package cz.trailsthroughshadows.api.table.schematic.location.model.dto;

import cz.trailsthroughshadows.api.table.schematic.part.model.PartDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "LocationPart")
public class LocationPartDTO {

    @EmbeddedId
    private LocationPartId key;

    @ManyToOne
    @JoinColumn(name = "idPart", insertable = false, updatable = false)
    private PartDTO part;

    @Column(nullable = false)
    private int rotation;

    @Embeddable
    @Data
    public static class LocationPartId implements Serializable {

        @Column(name = "idLocation")
        private int idLocation;

        @Column(name = "idPart")
        private int idPart;

    }
}
