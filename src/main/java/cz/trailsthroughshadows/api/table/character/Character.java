package cz.trailsthroughshadows.api.table.character;

import cz.trailsthroughshadows.api.table.campaign.Campaign;
import cz.trailsthroughshadows.api.table.character.clazz.Clazz;
import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import cz.trailsthroughshadows.api.table.character.race.Race;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCampaign")
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idClass")
    private Clazz clazz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRace")
    private Race location;

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
