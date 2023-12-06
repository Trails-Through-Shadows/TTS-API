package cz.trailsthroughshadows.api.table.playerdata;

import cz.trailsthroughshadows.api.table.character.clazz.Clazz;
import cz.trailsthroughshadows.api.table.character.race.Race;
import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import jakarta.persistence.*;
import lombok.*;

import java.util.Random;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Character")
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "idAdventure")
//    private Adventure adventure; // todo make Adventure class

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idClass")
    private Clazz clazz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRace")
    private Race race;

    @Column
    private int level;

    @Column
    private String name;

    @Column
    private String playerName;

    @Transient
    private Hex hex;

    public int roll() {
        return new Random().nextInt(1, 21);
    }
}
