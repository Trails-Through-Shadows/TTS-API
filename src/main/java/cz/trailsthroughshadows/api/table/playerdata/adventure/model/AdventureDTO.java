package cz.trailsthroughshadows.api.table.playerdata.adventure.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Description;
import cz.trailsthroughshadows.algorithm.validation.text.Title;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.campaign.Campaign;
import cz.trailsthroughshadows.api.table.playerdata.adventure.relation.AdventureAchievement;
import cz.trailsthroughshadows.api.table.playerdata.adventure.relation.AdventureLocation;
import cz.trailsthroughshadows.api.table.playerdata.adventure.relation.AdventureMarket;
import cz.trailsthroughshadows.api.table.playerdata.license.License;
import cz.trailsthroughshadows.api.table.playerdata.character.model.CharacterDTO;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Integer idLicense;

    @Column(insertable = false, updatable = false, nullable = false)
    private Integer idCampaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idLicense")
    private License license;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idCampaign")
    private Campaign campaign;


    @OneToMany(mappedBy = "id", fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private Collection<CharacterDTO> characters;

    @OneToMany(mappedBy = "key.idAdventure", fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private Collection<AdventureMarket> adventureMarkets;

    @OneToMany(mappedBy = "key.idAdventure", fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private Collection<AdventureLocation> adventureLocations;

    @OneToMany(mappedBy = "key.idAdventure", fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
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

        // Level must be positive.
        if (level < 0) {
            errors.add(new ValidationError("Adventure", "level", level, "Level must be positive."));
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
