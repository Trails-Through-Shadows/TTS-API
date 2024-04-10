package cz.trailsthroughshadows.api.table.background.clazz.model;

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
import cz.trailsthroughshadows.api.table.background.clazz.ClazzAction;
import cz.trailsthroughshadows.api.table.effect.relation.forcharacter.ClazzEffect;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Class")
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ClazzDTO extends Validable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(length = 32)
    private String tag;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private Integer baseHealth;

    @Column(nullable = false)
    private Integer baseDefence;

    @Column(nullable = false)
    private Integer baseInitiative;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "key.idClass", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private Collection<ClazzEffect> effects;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "key.idClass", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private Collection<ClazzAction> actions;

    @JsonIgnore
    public Collection<ActionDTO> getMappedActions() {
        if (actions == null) return null;
        return actions.stream().map(ClazzAction::getAction).toList();
    }

    //region Validation

    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Health, defence and initiative must not be null.
        if (baseHealth == null) {
            errors.add(new ValidationError("Class", "baseHealth", null, "Base health must not be null."));
        }
        if (baseDefence == null) {
            errors.add(new ValidationError("Class", "baseDefence", null, "Base defence must not be null."));
        }
        if (baseInitiative == null) {
            errors.add(new ValidationError("Class", "baseInitiative", null, "Base initiative must not be null."));
        }

        // Title, tag and description have to be valid.
        validateChild(new Title(title), validationConfig);
        validateChild(new Tag(tag), validationConfig);
        validateChild(new Description(description), validationConfig);

        // BaseHealth must be greater than 0.
        if (baseHealth != null && baseHealth <= 0) {
            errors.add(new ValidationError("Class", "baseHealth", getBaseHealth(), "Base health must be greater than 0."));
        }

        // BaseDefence must be greater than or equal to 0.
        if (baseDefence != null && baseDefence < 0) {
            errors.add(new ValidationError("Class", "baseDefence", getBaseDefence(), "Base defence must be greater than or equal to 0."));
        }

        // BaseInitiative must be greater than 0.
        if (baseInitiative != null && baseInitiative <= 0) {
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
