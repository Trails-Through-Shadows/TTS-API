package cz.trailsthroughshadows.api.table.schematic.location;

import cz.trailsthroughshadows.api.table.schematic.part.LocationPart;
import cz.trailsthroughshadows.api.table.schematic.part.Part;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Location")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "tag")
    private String tag;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Location.Type type;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "idLocation", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<LocationPart> locationParts;

    // Skipping n-to-n relationship, there is no additional data in that table
    @ToString.Include(name = "locationParts") // Including replacement field in toString
    public List<Part> getLocationParts() {
        if (locationParts == null) return null;
        return locationParts.stream().map(LocationPart::getPart).toList();
    }

    public enum Type {
        CITY, DUNGEON, MARKET, QUEST
    }

}

