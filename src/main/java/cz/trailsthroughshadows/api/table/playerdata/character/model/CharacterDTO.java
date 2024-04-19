package cz.trailsthroughshadows.api.table.playerdata.character.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Description;
import cz.trailsthroughshadows.algorithm.validation.text.Title;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CharacterDTO extends Validable implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idClass")
    protected ClazzDTO clazz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idRace")
    protected RaceDTO race;

    @Column(insertable = false, updatable = false, nullable = false)
    protected Integer idClass;

    @Column(insertable = false, updatable = false, nullable = false)
    protected Integer idRace;

    @Column(nullable = false)
    protected int idAdventure;

    @Column(nullable = false, length = 128)
    protected String title;

    @Column(length = 50)
    protected String playerName;

    @OneToMany(mappedBy = "key.idCharacter", fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    protected Collection<InventoryDTO> inventory;

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

    // region Validation

    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Title and playerName have to be valid using Title and Description standards.
        validateChild(new Title(title), validationConfig);
        validateChild(new Description(playerName), validationConfig, "Player name");

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

    // endregion
}
