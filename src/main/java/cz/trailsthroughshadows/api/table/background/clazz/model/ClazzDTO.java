package cz.trailsthroughshadows.api.table.background.clazz.model;

import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Description;
import cz.trailsthroughshadows.algorithm.validation.text.Tag;
import cz.trailsthroughshadows.algorithm.validation.text.Title;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.background.clazz.ClazzAction;
import cz.trailsthroughshadows.api.table.effect.relation.forcharacter.ClazzEffect;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Class")
public class ClazzDTO extends Validable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(length = 32)
    private String tag;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private int baseHealth;

    @Column(nullable = false)
    private int baseDefence;

    @Column(nullable = false)
    private int baseInitiative;

    @OneToMany
    @JoinColumn(name = "idClass")
    private Collection<ClazzEffect> effects;

    @OneToMany
    @JoinColumn(name = "idClass")
    private Collection<ClazzAction> actions;

    @ToString.Include(name = "actions")
    public Collection<ActionDTO> getActions() {
        if (actions == null) return null;
        return actions.stream().map(ClazzAction::getAction).toList();
    }

    //region Validation

    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Title, tag and description have to be valid.
        validateChild(new Title(title), validationConfig);
        validateChild(new Tag(tag), validationConfig);
        validateChild(new Description(description), validationConfig);

        // BaseHealth must be greater than 0.
        if (baseHealth <= 0) {
            errors.add(new ValidationError("Class", "baseHealth", getBaseHealth(), "Base health must be greater than 0."));
        }

        // BaseDefence must be greater than or equal to 0.
        if (baseDefence < 0) {
            errors.add(new ValidationError("Class", "baseDefence", getBaseDefence(), "Base defence must be greater than or equal to 0."));
        }

        // BaseInitiative must be greater than 0.
        // TODO fix base init when you figure it out
        if (baseInitiative <= 0) {
            errors.add(new ValidationError("Class", "baseInitiative", getBaseInitiative(), "Base initiative must be greater than 0."));
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
