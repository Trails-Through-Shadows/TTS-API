package cz.trailsthroughshadows.api.table.schematic.location;

import cz.trailsthroughshadows.algorithm.location.LocationImpl;
import cz.trailsthroughshadows.api.table.schematic.part.LocationPart;
import cz.trailsthroughshadows.api.table.schematic.part.Part;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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


    //@OneToMany(mappedBy = "key.idPart", cascade = CascadeType.ALL)
    @OneToMany(mappedBy = "key.idLocation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<LocationPart> locationParts;

    @Override
    public List<Part> getParts() {
        if (locationParts == null) {
            return null;
        }
        return locationParts.stream().map(LocationPart::getPart).toList();
    }
}

