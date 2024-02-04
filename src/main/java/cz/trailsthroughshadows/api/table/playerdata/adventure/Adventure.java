package cz.trailsthroughshadows.api.table.playerdata.adventure;

import cz.trailsthroughshadows.api.table.campaign.Campaign;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`Adventure`")
@Entity
public class Adventure {
    @Id
    private Integer id;
    @Column(nullable = false)
    private int reputation;
    @Column(nullable = false)
    private int experience;
    @Column(nullable = false)
    private int gold;
    @Column(nullable = false, length = 128)
    private String title;

    @Column(insertable = false, updatable = false, nullable = false)
    private int idLicense;
    @Column(insertable = false, updatable = false, nullable = false)
    private int idCampaign;

    @ManyToOne
    @JoinColumn(name = "idLicense")
    private License license;

    @ManyToOne
    @JoinColumn(name = "idCampaign")
    private Campaign campaign;

    // Not healthy
//    @OneToMany
//    @JoinColumn(name = "idAdventure")
//    private Collection<Character> characters;


    @OneToMany(mappedBy = "key.idAdventure")
    private Collection<AdventureMarket> adventureMarkets;

    @OneToMany(mappedBy = "key.idAdventure")
    private Collection<AdventureLocation> adventureLocations;

    @OneToMany(mappedBy = "key.idAdventure")
    private Collection<AdventureAchievement> adventureAchievements;

}
