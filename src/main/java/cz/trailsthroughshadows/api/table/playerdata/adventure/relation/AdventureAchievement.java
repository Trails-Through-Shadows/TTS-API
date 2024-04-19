package cz.trailsthroughshadows.api.table.playerdata.adventure.relation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.trailsthroughshadows.api.table.playerdata.adventure.achievement.AchievementDTO;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AdventureAchievement {

    @EmbeddedId
    private AdventureAchievementId key;

    @Column(nullable = false)
    private int progress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idAchievement", insertable = false, updatable = false)
    private AchievementDTO achievement;

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
