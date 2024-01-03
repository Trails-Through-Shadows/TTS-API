package cz.trailsthroughshadows.api.table.schematic.part;

import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
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

    public int compareTo(Part other, String sort) {
        String[] sortSplit = sort.split(":");
        String sortKey = sortSplit[0];
        String sortDirection = sortSplit[1];

        switch (sortKey) {
            case "id" -> {
                if (sortDirection.equals("asc")) {
                    return Integer.compare(this.id, other.id);
                } else {
                    return Integer.compare(other.id, this.id);
                }
            }

            case "tag" -> {
                if (sortDirection.equals("asc")) {
                    return this.tag.compareTo(other.tag);
                } else {
                    return other.tag.compareTo(this.tag);
                }
            }

            case "usages" -> {
                if (sortDirection.equals("asc")) {
                    return Integer.compare(this.usages, other.usages);
                } else {
                    return Integer.compare(other.usages, this.usages);
                }
            }

            default -> {
                return 0;
            }
        }
    }
}