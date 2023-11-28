package cz.trailsthroughshadows.api.table.schematic.location;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Location")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    enum LocationType {
        CITY,
        DUNGEON,
        MARKET,
        QUEST,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 30)
    private String tag;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationType type;

    @Column
    private String description;
}
