package cz.trailsthroughshadows.api.table.schematic.part;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import cz.trailsthroughshadows.api.table.schematic.hex.PartDoor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Part")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tag")
    private String tag;

    @OneToMany(mappedBy = "key.idPart", cascade = CascadeType.ALL)
    private Set<Hex> hexes;

    @OneToMany(mappedBy = "key.fromPart", cascade = CascadeType.ALL)
    private Set<PartDoor> doors;

    @Column(name = "usages", columnDefinition = "INT default 0")
    private int usages = 0;


}