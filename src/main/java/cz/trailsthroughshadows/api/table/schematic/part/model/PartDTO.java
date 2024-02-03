package cz.trailsthroughshadows.api.table.schematic.part.model;

import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Part")
public class PartDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tag")
    private String tag;

    @OneToMany(mappedBy = "key.idPart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hex> hexes = new ArrayList<>();

    @Column(name = "usages", columnDefinition = "INT default 0")
    private int usages = 0;

    public void setHexes(List<Hex> hexes) {
        if (hexes != null) {
            this.hexes.retainAll(hexes);
            this.hexes.addAll(hexes);
        } else {
            this.hexes.clear();
        }
    }
}