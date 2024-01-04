package cz.trailsthroughshadows.api.table.schematic.hex;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HexDoor")
public class HexDoor {
    @Id
    private int id;

    
    @Column(name = "idLocation", nullable = false)
    private int idLocation;

    @Column(name = "firstPart", nullable = false)
    private int firstPart;

    @Column(name = "secondPart", nullable = false)
    private int secondPart;

    @Column(name = "firstHex", nullable = false)
    private int firstHex;

    @Column(name = "secondHex", nullable = false)
    private int secondHex;
}
