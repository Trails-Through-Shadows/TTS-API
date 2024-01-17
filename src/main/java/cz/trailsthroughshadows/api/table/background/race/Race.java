package cz.trailsthroughshadows.api.table.background.race;

import cz.trailsthroughshadows.api.table.effect.forcharacter.RaceEffect;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Race")
public class Race {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @OneToMany
    @JoinColumn(name = "idRace")
    private Collection<RaceEffect> effects;

    @OneToMany
    @JoinColumn(name = "idRace")
    private Collection<RaceAction> actions;
}
