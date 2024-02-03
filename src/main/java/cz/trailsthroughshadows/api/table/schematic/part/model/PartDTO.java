package cz.trailsthroughshadows.api.table.schematic.part.model;

import cz.trailsthroughshadows.api.table.schematic.hex.model.HexDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Part")
@Getter
public class PartDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tag")
    private String tag;

    @OneToMany(mappedBy = "key.idPart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HexDTO> hexes = new ArrayList<>();

    @Column(name = "usages", columnDefinition = "INT default 0")
    private int usages = 0;

    public void setHexes(List<HexDTO> hexes) {
        if (hexes != null) {
            this.hexes.retainAll(hexes);
            this.hexes.addAll(hexes);
        } else {
            this.hexes.clear();
        }
    }
}