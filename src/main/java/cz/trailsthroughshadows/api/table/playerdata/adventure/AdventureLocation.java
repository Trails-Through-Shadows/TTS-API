package cz.trailsthroughshadows.api.table.playerdata.adventure;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`AdventureLocation`")
@Entity
public class AdventureLocation {

    @EmbeddedId
    private AdventureLocationId key;
    @Column(nullable = false)
    private boolean unlocked;
    @Column(nullable = false)
    private int timesVisited;

    @Embeddable
    @Data
    @NoArgsConstructor
    public static class AdventureLocationId implements Serializable {
        @Column(nullable = false)
        private Integer idAdventure;
        @Column(nullable = false)
        private Integer idLocation;
    }
}
