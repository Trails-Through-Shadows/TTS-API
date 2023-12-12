package cz.trailsthroughshadows.api.table.schematic.location;

import cz.trailsthroughshadows.algorithm.location.LocationImpl;
import cz.trailsthroughshadows.api.table.schematic.part.LocationPart;
import cz.trailsthroughshadows.api.table.schematic.part.Part;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Location")
@EqualsAndHashCode(callSuper = true)
public class Location extends LocationImpl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "title", nullable = false)
    public String title;

    @Column(name = "tag")
    public String tag;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    public Location.Type type;

    @Column(name = "description", columnDefinition = "TEXT")
    public String description;

    @OneToMany(mappedBy = "idLocation", fetch = FetchType.LAZY)
    @ToString.Exclude
    public List<LocationPart> locationParts;

    // Skipping n-to-n relationship, there is no additional data in that table
    @Override
    @ToString.Include(name = "locationParts") // Including replacement field in toString
    public List<Part> getLocationParts() {
        if (locationParts == null) return null;
        return locationParts.stream().map(LocationPart::getPart).toList();
    }

}

