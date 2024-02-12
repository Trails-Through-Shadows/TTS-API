package cz.trailsthroughshadows.api.table.playerdata.adventure.model;

import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Description;
import cz.trailsthroughshadows.algorithm.validation.text.Title;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.campaign.Campaign;
import cz.trailsthroughshadows.api.table.playerdata.adventure.AdventureAchievement;
import cz.trailsthroughshadows.api.table.playerdata.adventure.AdventureLocation;
import cz.trailsthroughshadows.api.table.playerdata.adventure.AdventureMarket;
import cz.trailsthroughshadows.api.table.playerdata.adventure.License;
import jakarta.annotation.Nullable;
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
public class AdventureDTO extends Validable {
    @Id
    private Integer id;

    @Column(nullable = false)
    private int reputation;

    @Column(nullable = false)
    private int experience;

    @Column(nullable = false)
    private int gold;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(nullable = true)
    private String description;

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

    //region Validation

    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Title and description have to be valid.
        validateChild(new Title(title), validationConfig);
        validateChild(new Description(description), validationConfig);

        // Reputation must be within bounds.
        int min = validationConfig.getAdventure().getMinReputation();
        int max = validationConfig.getAdventure().getMaxReputation();
        if (reputation < min || reputation > max) {
            errors.add(new ValidationError("Adventure", "reputation", reputation, "Reputation must be between " + min + " and " + max + "."));
        }

        // Experience must be positive.
        if (experience < 0) {
            errors.add(new ValidationError("Adventure", "experience", experience, "Experience must be positive."));
        }

        // Gold must be positive.
        if (gold < 0) {
            errors.add(new ValidationError("Adventure", "gold", gold, "Gold must be positive."));
        }

        // Level must be greater than 0.
        if (level <= 0) {
            errors.add(new ValidationError("Adventure", "level", level, "Level must be greater than 0."));
        }

        // All characters must be validated.
        // TODO zozeee get characters from adventure
    }

    @Override
    public String getValidableValue() {
        return getTitle();
    }

    //endregion
}