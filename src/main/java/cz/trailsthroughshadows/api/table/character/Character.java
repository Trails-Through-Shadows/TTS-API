package cz.trailsthroughshadows.api.table.character;

import cz.trailsthroughshadows.api.table.action.Action;
import cz.trailsthroughshadows.api.table.character.clazz.Clazz;
import cz.trailsthroughshadows.api.table.character.race.Race;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`Character`")
@Entity
public class Character extends cz.trailsthroughshadows.algorithm.entity.Entity implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "idClass")
    private Clazz clazz;

    @ManyToOne()
    @JoinColumn(name = "idRace")
    private Race race;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String playerName;

    @Override
    public Character clone() {
        Character character = new Character();

        character.setId(this.getId());
        character.setClazz(this.getClazz());
        character.setRace(this.getRace());
        character.setLevel(this.getLevel());
        character.setName(this.getName());
        character.setPlayerName(this.getPlayerName());

        return character;
    }

    @Override
    public List<Action> getActions() {
        // todo zozeeeeeee
        return new ArrayList<>();
    }
}
