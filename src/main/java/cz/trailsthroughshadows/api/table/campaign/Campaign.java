package cz.trailsthroughshadows.api.table.campaign;

import cz.trailsthroughshadows.api.table.achievement.Achievement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Campaign")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 128)
    private String title;

    @Column
    private String description;

    @OneToMany
    @JoinColumn(name = "idCampaign")
    private Collection<CampaignAchievements> achievements;

    @OneToMany
    @JoinColumn(name = "idCampaign")
    private Collection<CampaignLocation> locations;

    @ToString.Include(name = "achievements") // Including replacement field in toString
    public Collection<Achievement> getAchievements() {
        if (achievements == null) return null;
        return achievements.stream().map(CampaignAchievements::getAchievement).toList();
    }

}
