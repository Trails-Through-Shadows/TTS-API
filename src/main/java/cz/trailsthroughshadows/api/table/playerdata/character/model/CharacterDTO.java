package cz.trailsthroughshadows.api.table.playerdata.character.model;

import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Title;
import cz.trailsthroughshadows.api.table.background.clazz.model.ClazzDTO;
import cz.trailsthroughshadows.api.table.background.race.model.RaceDTO;
import cz.trailsthroughshadows.api.table.playerdata.character.inventory.InventoryDTO;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`Character`")
@Entity
public class CharacterDTO extends Validable implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "idClass")
    private ClazzDTO clazz;

    @ManyToOne()
    @JoinColumn(name = "idRace")
    private RaceDTO race;

    @Column(nullable = false)
    private int idAdventure;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(nullable = false, length = 50)
    private String playerName;

    @OneToMany
    @JoinColumn(name = "idCharacter")
    private Collection<InventoryDTO> inventory;

    @Override
    public CharacterDTO clone() {
        CharacterDTO character = new CharacterDTO();

        character.setId(this.getId());
        character.setClazz(this.getClazz());
        character.setRace(this.getRace());
        character.setTitle(this.getTitle());
        character.setPlayerName(this.getPlayerName());

        return character;
    }

    //region Validation

    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Title and playerName have to be valid using Title standards.
        validateChild(new Title(title), validationConfig);
        validateChild(new Title(playerName), validationConfig);

        // Race and class must be validated.
        validateChild(clazz, validationConfig);
        validateChild(race, validationConfig);

        // All items must be validated.
        inventory.forEach(item -> validateChild(item, validationConfig));
    }

    @Override
    public String getValidableValue() {
        return getTitle() + " (" + getPlayerName() + ")";
    }

    //endregion
}