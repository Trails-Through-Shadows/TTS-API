package cz.trailsthroughshadows.api.table.playerdata.adventure;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`AdventureAchievements`")
@Entity
public class AdventureAchievements {

    @EmbeddedId
    private AdventureAchievementsId key;
    @Column(nullable = false)
    private int progress;

    @Embeddable
    @Data
    @NoArgsConstructor
    public static class AdventureAchievementsId implements Serializable {
        @Column(nullable = false)
        private Integer idAdventure;
        @Column(nullable = false)
        private Integer idAchievement;
    }
}
