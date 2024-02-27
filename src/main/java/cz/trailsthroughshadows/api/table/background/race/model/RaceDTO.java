package cz.trailsthroughshadows.api.table.background.race.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Description;
import cz.trailsthroughshadows.algorithm.validation.text.Tag;
import cz.trailsthroughshadows.algorithm.validation.text.Title;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.background.race.RaceAction;
import cz.trailsthroughshadows.api.table.effect.relation.forcharacter.RaceEffect;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.stream.Collectors;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Race")
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RaceDTO extends Validable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(nullable = false, length = 128)
    public String title;

    @Column(nullable = false)
    public Integer baseInitiative;

    @JsonSerialize(using = LazyFieldsSerializer.class)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "key.idRace")
    public Collection<RaceEffect> effects;

    @JsonSerialize(using = LazyFieldsSerializer.class)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "key.idRace")
    public Collection<RaceAction> actions;

    @Column(length = 32)
    private String tag;

    @Column
    private String description;

    @JsonIgnore
    public Collection<ActionDTO> getMappedActions() {
        if (actions == null) return null;
        return actions.stream().map(RaceAction::getAction).collect(Collectors.toList());
    }

    //region Validation
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // BaseInitiative must not be null.
        if (baseInitiative == null) {
            errors.add(new ValidationError("Race", "baseInitiative", null, "Base initiative must not be null."));
        }

        // Title, tag and description have to be valid.
        validateChild(new Title(title), validationConfig);
        validateChild(new Tag(tag), validationConfig);
        validateChild(new Description(description), validationConfig);

        // BaseInitiative must be greater than 0.
        if (baseInitiative != null && baseInitiative <= 0) {
            errors.add(new ValidationError("Race", "baseInitiative", baseInitiative, "Base initiative must be greater than 0."));
        }

        // All actions and effects must be validated.
        actions.forEach(action -> validateChild(action.getAction(), validationConfig));
        effects.forEach(effect -> validateChild(effect.getEffect(), validationConfig));
    }

    @Override
    public String getValidableValue() {
        return getTitle();
    }

    //endregion
}
