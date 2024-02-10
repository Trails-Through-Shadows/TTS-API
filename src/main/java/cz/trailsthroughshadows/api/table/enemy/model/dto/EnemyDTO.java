package cz.trailsthroughshadows.api.table.enemy.model.dto;

import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Tag;
import cz.trailsthroughshadows.algorithm.validation.text.Title;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.api.rest.jsonfilter.LazyFieldsFilter;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.forothers.EnemyEffect;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Enemy")
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EnemyDTO extends Validable implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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

    @Column
    private Integer usages;

    @OneToMany(mappedBy = "idEnemy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EnemyEffect> effects;

    @OneToMany(mappedBy = "idEnemy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EnemyActionDTO> actions;

    public List<EffectDTO> getEffects() {
        if (effects == null) return new ArrayList<>();
        return effects.stream().map(EnemyEffect::getEffect).toList();
    }

    public List<ActionDTO> getActions() {
        if (actions == null) return new ArrayList<>();
        return actions.stream().map(EnemyActionDTO::getAction).toList();
    }

    public void loadAll() {
        Initialization.hibernateInitializeAll(this);
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

    //region Validation
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {

        // Title and tag have to be valid.
        Title title = new Title(getTitle());
        validateChild(title, validationConfig);

        Tag tag = new Tag(getTag());
        validateChild(tag, validationConfig);

        // Health must be greater than 0.
        if (getBaseHealth() <= 0) {
            errors.add(new ValidationError("Enemy", "baseHealth", getBaseHealth(), "Base health must be greater than 0!"));
        }

        // All actions and effects must be validated.
        List<ActionDTO> actions = getActions();
        List<EffectDTO> effects = getEffects();

        for (ActionDTO action : actions) {
            validateChild(action, validationConfig);
        }
        for (EffectDTO effect : effects) {
            validateChild(effect, validationConfig);
        }

        if (!errors.isEmpty()) return;

        // Enemy can't have multiple of the same action or base effect.
        for (int i = 0; i < actions.size(); i++) {
            for (int j = i + 1; j < actions.size(); j++) {
                if (actions.get(i).equals(actions.get(j))) {
                    errors.add(new ValidationError("Enemy", "actions", actions.get(i).getValidableValue(), "Enemy can't have multiple of the same action!"));
                }
            }
        }
        for (int i = 0; i < effects.size(); i++) {
            for (int j = i + 1; j < effects.size(); j++) {
                if (effects.get(i).equals(effects.get(j))) {
                    errors.add(new ValidationError("Enemy", "effects", effects.get(i).getValidableValue(), "Enemy can't have multiple of the same base effect!"));
                }
            }
        }
    }

    @Override
    public String getValidableValue() {
        return getTitle();
    }
    //endregion
}
