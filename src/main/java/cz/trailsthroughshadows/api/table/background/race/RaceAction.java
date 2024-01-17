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
    private Integer id;
    @Column(nullable = false)
    private int idRace;
    @Column
    private Integer levelReq;
    @ManyToOne
    @JoinColumn(name = "idAction")
    private Action action;
}
