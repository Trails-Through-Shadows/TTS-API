package cz.trailsthroughshadows.api.table.schematic.part;

import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import cz.trailsthroughshadows.api.table.schematic.hex.HexDoor;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
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

    @OneToMany(mappedBy = "firstPart")
    private Set<HexDoor> firstPart;

    @Transient
    private int usages = 0;

}