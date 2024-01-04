package cz.trailsthroughshadows.api.table.schematic.part;

import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import cz.trailsthroughshadows.api.table.schematic.hex.HexDoor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
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
    private Collection<Hex> hexes;

//    @Query("SELECT h FROM HexDoor h WHERE :id = h.firstPart OR :id = h.secondPart")
//    public List<HexDoor> hexDoors(@Param("id") int id);


    @Transient
    private int usages = 0;

}