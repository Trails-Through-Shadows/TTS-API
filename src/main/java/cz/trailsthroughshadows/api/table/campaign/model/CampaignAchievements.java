package cz.trailsthroughshadows.api.table.campaign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.table.playerdata.adventure.achievement.AchievementDTO;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CampaignAchievements {

    @EmbeddedId
    private CampaignAchievementsId key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idAchievement", insertable = false, updatable = false)
    private AchievementDTO achievement;

    @Embeddable
    @Data
    public static class CampaignAchievementsId implements Serializable {
        @Column(nullable = false)
        private Integer idCampaign;

        @Column(nullable = false)
        private Integer idAchievement;
    }
}
