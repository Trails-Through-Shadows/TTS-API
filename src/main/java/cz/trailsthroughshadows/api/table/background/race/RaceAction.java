package cz.trailsthroughshadows.api.table.background.race;

import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "RaceAction")
public class RaceAction {

    @EmbeddedId
    private RaceActionId key;

    @ManyToOne
    @JoinColumn(name = "idAction", insertable = false, updatable = false)
    private ActionDTO action;

    @Embeddable
    @Data
    public static class RaceActionId implements Serializable {
        @Column(nullable = false)
        private int idRace;

        @Column(nullable = false)
        private int idAction;
    }

}
