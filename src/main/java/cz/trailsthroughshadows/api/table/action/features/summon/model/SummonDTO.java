package cz.trailsthroughshadows.api.table.action.features.summon.model;

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
import cz.trailsthroughshadows.api.table.effect.relation.foraction.SummonEffect;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Summon")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SummonDTO extends Validable implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 128)
    private String title;

    @Column
    private Integer duration;

    @Column
    private Integer health;

    @Column(length = 32)
    private String tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idAction")
    private ActionDTO action;


    @OneToMany(mappedBy = "key.idSummon", fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private Collection<SummonEffect> effects;

    @JsonIgnore
    public Collection<EffectDTO> getMappedEffects() {
        if (effects == null) return null;
        return effects.stream().map(SummonEffect::getEffect).toList();
    }

    @Override
    public SummonDTO clone() {
        SummonDTO summon = new SummonDTO();

        summon.setId(this.getId());
        summon.setTitle(this.getTitle());
        summon.setDuration(this.getDuration());
        summon.setHealth(this.getHealth());
        summon.setAction(this.getAction());
        summon.setEffects(this.getEffects());

        return summon;
    }

    //region Validation
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Title and tag have to be valid.
        Title title = new Title(getTitle());
        validateChild(title, validationConfig);

        Tag tag = new Tag(getTag());
        validateChild(tag, validationConfig);

        // Duration must be greater than 0.
        if (getDuration() <= 0) {
            errors.add(new ValidationError("Summon", "duration", getDuration(), "Duration must be greater than 0!"));
        }

        // Health must be greater than 0.
        if (getHealth() <= 0) {
            errors.add(new ValidationError("Summon", "health", getHealth(), "Health must be greater than 0!"));
        }

        // The action and all effects must be validated.
        validateChild(getAction(), validationConfig);
        for (var e : getMappedEffects()) {
            validateChild(e, validationConfig);
        }
    }

    @Override
    public String getValidableValue() {
        return getTitle();
    }
    //endregion
}
