package cz.trailsthroughshadows.api.table.schematic.part;

import cz.trailsthroughshadows.api.table.schematic.location.Location;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Part")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @ManyToMany(mappedBy = "parts", fetch = FetchType.EAGER) // TODO: EAGER is not good, we need LAZY
    private List<Location> locations;

    @Column(length = 30)
    private String tag;
}

