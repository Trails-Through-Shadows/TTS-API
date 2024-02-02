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
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tag")
    private String tag;

    @OneToMany(mappedBy = "key.idPart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hex> hexes = new ArrayList<>();

    public void setHexes(List<Hex> hexes) {
        if (this.hexes == null)
            this.hexes = hexes;

        this.hexes.clear();
        this.hexes.addAll(hexes);

    }

    @Column(name = "usages", columnDefinition = "INT default 0")
    private int usages = 0;

}