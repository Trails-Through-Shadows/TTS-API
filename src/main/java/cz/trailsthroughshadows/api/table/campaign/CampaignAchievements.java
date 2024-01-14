package cz.trailsthroughshadows.api.table.campaign;

import cz.trailsthroughshadows.api.table.achievement.Achievement;
import cz.trailsthroughshadows.api.table.effect.Effect;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CampaignAchievements")
public class CampaignAchievements {

    @EmbeddedId
    private CampaignAchievementsId key;

    @ManyToOne
    @JoinColumn(name = "idAchievement", insertable = false, updatable = false)
    private Achievement achievement;

    @Embeddable
    public static class CampaignAchievementsId implements Serializable {
        @Column(nullable = false)
        private Integer idCampaign;
        @Column(nullable = false)
        private Integer idAchievement;
    }
}
