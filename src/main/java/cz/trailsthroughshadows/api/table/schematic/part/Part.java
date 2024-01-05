package cz.trailsthroughshadows.api.table.schematic.part;

import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import cz.trailsthroughshadows.api.table.schematic.hex.PartDoor;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Part")
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "tag")
    private String tag;

    @OneToMany(mappedBy = "key.idPart")
    private Set<Hex> hexes;

    @OneToMany(mappedBy = "key.fromPart")
    private Set<PartDoor> doors;

    @Transient
    private int usages = 0;

}