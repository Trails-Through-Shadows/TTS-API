package cz.trailsthroughshadows.api.table.playerdata.adventure.relation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`AdventureAchievement`")
@Entity
public class AdventureAchievement {

    @EmbeddedId
    private AdventureAchievementId key;

    @Column(nullable = false)
    private int progress;

    @Embeddable
    @Data
    @NoArgsConstructor
    public static class AdventureAchievementId implements Serializable {
        @Column(nullable = false)
        private Integer idAdventure;

        @Column(nullable = false)
        private Integer idAchievement;
    }
}
