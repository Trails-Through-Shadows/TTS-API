package cz.trailsthroughshadows.api.table.schematic.location;

import cz.trailsthroughshadows.api.table.schematic.part.Part;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Location")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int id;

    @ManyToMany(fetch = FetchType.EAGER) // TODO: EAGER is not good, we need LAZY
    @JoinTable(
            name = "LocationPart",
            joinColumns = @JoinColumn(name = "idLocation"),
            inverseJoinColumns = @JoinColumn(name = "idPart")
    )
    private List<Part> parts;

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
