package cz.trailsthroughshadows.api.table.campaign.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.table.playerdata.adventure.achievement.AchievementDTO;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "Campaign")
public class CampaignDTO extends Validable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 128)
    private String title;

    @Column
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "key.idCampaign", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private List<CampaignAchievements> achievements;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "idCampaign", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private List<CampaignLocation> locations;

    @JsonIgnore
    public Collection<AchievementDTO> getMappedAchievements() {
        if (achievements == null)
            return null;
        return achievements.stream().map(CampaignAchievements::getAchievement).toList();
    }

    @JsonIgnore
    public Collection<LocationDTO> getMappedLocations() {
        if (locations == null)
            return null;
        return locations.stream().map(CampaignLocation::getLocation).toList();
    }

//    @JsonIgnore
//    public List<CampaignLocation.Condition> getConditions(Integer locationId) {
//        return locations.stream()
//                .filter(l -> l.getLocation().getId().equals(locationId))
//                .map(CampaignLocation::getCondition)
//                .orElse(null);
//    }

    //region Validation

    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {

    }

    @Override
    public String getValidableValue() {
        return "";
    }
    //endregion
}
