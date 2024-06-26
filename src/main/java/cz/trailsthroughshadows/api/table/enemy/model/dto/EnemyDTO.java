package cz.trailsthroughshadows.api.table.enemy.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Tag;
import cz.trailsthroughshadows.algorithm.validation.text.Title;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.forothers.EnemyEffectDTO;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Enemy")
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EnemyDTO extends Validable implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(length = 32)
    private String tag;

    @Column
    private String description;

    @Column(nullable = false)
    private Integer baseHealth;

    @Column(nullable = false)
    private Integer baseDefence;

    @Column(nullable = false)
    private Integer baseInitiative;

    @Column
    private Integer usages;

    @OneToMany(mappedBy = "key.idEnemy", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private List<EnemyEffectDTO> effects = new ArrayList<>();

    @OneToMany(mappedBy = "key.idEnemy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private List<EnemyActionDTO> actions = new ArrayList<>();


    public void setEffects(List<EnemyEffectDTO> effects) {
        this.effects.clear();
        if (effects != null) {
            this.effects.addAll(effects);
            this.effects.forEach(effect -> effect.getKey().setIdEnemy(this.getId()));
        }
    }

    public void setActions(List<EnemyActionDTO> actions) {
        this.actions.clear();
        if (actions != null) {
            this.actions.addAll(actions);
            this.actions.forEach(action -> action.getKey().setIdEnemy(this.getId()));
        }
    }

    @JsonIgnore
    public List<EffectDTO> getMappedEffects() {
        if (effects == null)
            return new ArrayList<>();
        return effects.stream().map(EnemyEffectDTO::getEffect).toList();
    }

    @JsonIgnore
    public List<ActionDTO> getMappedActions() {
        if (actions == null)
            return new ArrayList<>();
        return actions.stream()
                .peek(action -> action.getKey().setIdEnemy(this.getId()))
                .map(EnemyActionDTO::getAction)
                .toList();
    }

    @Override
    public EnemyDTO clone() {
        EnemyDTO enemy = new EnemyDTO();

        enemy.setId(this.getId());
        enemy.setTitle(this.getTitle());
        enemy.setBaseDefence(this.getBaseDefence());
        enemy.setBaseHealth(this.getBaseHealth());

        return enemy;
    }

    // region Validation
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Health, defence and initiative must not be null.
        if (baseHealth == null) {
            errors.add(new ValidationError("Enemy", "baseHealth", null, "Base health must not be null."));
        }
        if (baseDefence == null) {
            errors.add(new ValidationError("Enemy", "baseDefence", null, "Base defence must not be null."));
        }
        if (baseInitiative == null) {
            errors.add(new ValidationError("Enemy", "baseInitiative", null, "Base initiative must not be null."));
        }

        // Title and tag have to be valid.
        Title title = new Title(getTitle());
        validateChild(title, validationConfig);

        Tag tag = new Tag(getTag());
        validateChild(tag, validationConfig);

        // Health must be greater than 0.
        if (baseHealth != null && getBaseHealth() <= 0) {
            errors.add(
                    new ValidationError("Enemy", "baseHealth", getBaseHealth(), "Base health must be greater than 0!"));
        }

        // All actions and effects must be validated.
        List<ActionDTO> actions = getMappedActions();
        List<EffectDTO> effects = getMappedEffects();

        for (ActionDTO action : actions) {
            validateChild(action, validationConfig);
        }
        for (EffectDTO effect : effects) {
            validateChild(effect, validationConfig);
        }

        if (!errors.isEmpty())
            return;

        // Enemy can't have multiple of the same action or base effect.
        for (int i = 0; i < actions.size(); i++) {
            for (int j = i + 1; j < actions.size(); j++) {
                if (actions.get(i).equals(actions.get(j))) {
                    errors.add(new ValidationError("Enemy", "actions", actions.get(i).getValidableValue(),
                            "Enemy can't have multiple of the same action!"));
                }
            }
        }
        for (int i = 0; i < effects.size(); i++) {
            for (int j = i + 1; j < effects.size(); j++) {
                if (effects.get(i).equals(effects.get(j))) {
                    errors.add(new ValidationError("Enemy", "effects", effects.get(i).getValidableValue(),
                            "Enemy can't have multiple of the same base effect!"));
                }
            }
        }
    }

    @Override
    public String getValidableValue() {
        return getTitle();
    }
    // endregion
}
