package cz.trailsthroughshadows.api.table.background.race;

import cz.trailsthroughshadows.api.table.action.Action;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "RaceAction")
public class RaceAction {
    @Id
    @Column(nullable = false)
    private int idRace;

    @Id
    @Column(nullable = false)
    private int idAction;

    @ManyToOne
    @JoinColumn(name = "idAction", insertable = false, updatable = false)
    private Action action;

}
