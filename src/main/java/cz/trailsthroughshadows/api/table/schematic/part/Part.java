package cz.trailsthroughshadows.api.table.schematic.part;

import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @OneToMany(mappedBy = "key.idPart", fetch = FetchType.LAZY)
    private List<Hex> hexes;

    @Transient
    private int usages = 0;

}