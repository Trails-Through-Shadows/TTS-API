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

    @Id
    @Column(name = "idLocation")
    private int idLocation;

    @Id
    @Column(name = "idPart")
    private int idPart;

    @ManyToOne
    @JoinColumn(name = "idPart", insertable = false, updatable = false)
    private Part part;

}
