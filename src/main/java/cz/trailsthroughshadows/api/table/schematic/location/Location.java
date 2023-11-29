package cz.trailsthroughshadows.api.table.schematic.location;

import cz.trailsthroughshadows.api.table.schematic.part.LocationPart;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Location")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int id;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "LocationPart", joinColumns = @JoinColumn(name = "idLocation"))
    private List<LocationPart> parts;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 30)
    private String tag;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationType type;

    @Column
    private String description;

    enum LocationType {
        CITY,
        DUNGEON,
        MARKET,
        QUEST,
    }
}
