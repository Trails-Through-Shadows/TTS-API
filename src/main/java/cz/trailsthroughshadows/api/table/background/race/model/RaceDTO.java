package cz.trailsthroughshadows.api.table.background.race.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Description;
import cz.trailsthroughshadows.algorithm.validation.text.Tag;
import cz.trailsthroughshadows.algorithm.validation.text.Title;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsFilter;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.background.race.RaceAction;
import cz.trailsthroughshadows.api.table.effect.relation.forcharacter.RaceEffect;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Race")
public class RaceDTO extends Validable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    public Integer id;

    @Column(nullable = false, length = 128)
    public String title;

    @Column(nullable = false)
    public int baseInitiative;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "idRace")
    public Collection<RaceEffect> effects;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "idRace")
    @ToString.Exclude
    public Collection<RaceAction> actions;

    @Column(length = 32)
    private String tag;

    @Column
    private String description;

    @ToString.Include(name = "actions")
    public Collection<ActionDTO> getActions() {
        if (actions == null) return null;
        return actions.stream().map(RaceAction::getAction).collect(Collectors.toList());
    }

    //region Validation

    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Title, tag and description have to be valid.
        validateChild(new Title(title), validationConfig);
        validateChild(new Tag(tag), validationConfig);
        validateChild(new Description(description), validationConfig);

        // BaseInitiative must be greater than 0.
        // TODO fix base init when you figure it out
        if (baseInitiative <= 0) {
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
