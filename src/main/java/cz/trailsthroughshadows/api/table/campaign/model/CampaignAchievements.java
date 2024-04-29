package cz.trailsthroughshadows.api.table.campaign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.table.playerdata.adventure.achievement.AchievementDTO;
import jakarta.annotation.Nullable;
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
public class CampaignAchievements extends Validable {

    @EmbeddedId
    private CampaignAchievementsId key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idAchievement", insertable = false, updatable = false)
    private AchievementDTO achievement;

    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        if (key == null) {
            errors.add(new cz.trailsthroughshadows.api.rest.model.error.type.ValidationError("CampaignAchievements", "key", null, "Key must not be null."));
        }
        if (!errors.isEmpty()) { return; }
        if (key.idAchievement == null) {
            errors.add(new cz.trailsthroughshadows.api.rest.model.error.type.ValidationError("CampaignAchievements", "key.idAchievement", null, "Achievement ID must not be null."));
        }
        if (key.idCampaign == null) {
            errors.add(new cz.trailsthroughshadows.api.rest.model.error.type.ValidationError("CampaignAchievements", "key.idCampaign", null, "Campaign ID must not be null."));
        }
        if (achievement == null) {
            errors.add(new cz.trailsthroughshadows.api.rest.model.error.type.ValidationError("CampaignAchievements", "achievement", null, "Achievement must not be null."));
        }
        validateChild(achievement, validationConfig);
        if (!errors.isEmpty()) { return; }
        if (achievement.getId() == null) {
            errors.add(new cz.trailsthroughshadows.api.rest.model.error.type.ValidationError("CampaignAchievements", "achievement.id", null, "Achievement ID must not be null."));
        }
    }

    @Override
    public String getValidableValue() {
        return null;
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CampaignAchievementsId implements Serializable {
        @Column(nullable = false)
        private Integer idCampaign;

        @Column(nullable = false)
        private Integer idAchievement;
    }
}
