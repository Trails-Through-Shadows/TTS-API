package cz.trailsthroughshadows.api.table.action.features.summon;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Tag;
import cz.trailsthroughshadows.algorithm.validation.text.Title;
import cz.trailsthroughshadows.api.rest.jsonfilter.LazyFieldsFilter;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.foraction.SummonEffect;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Summon")
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
public class Summon extends Validable implements Cloneable {

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

    @ManyToOne()
    @JoinColumn(name = "idAction")
    private ActionDTO action;
    @OneToMany(mappedBy = "idSummon")
    @ToString.Exclude
    private Collection<SummonEffect> effects;

//    @OneToMany(mappedBy = "summon")
//    private Collection<SummonAction> actions;

    // Skipping n-to-n relationship, there is no additional data in that table
    @ToString.Include(name = "effects") // Including replacement field in toString
    public Collection<EffectDTO> getEffects() {
        if (effects == null) return null;
        return effects.stream().map(SummonEffect::getEffect).toList();
    }

    public Collection<SummonEffect> getRawEffects() {
        return effects;
    }

    @Override
    public Summon clone() {
        Summon summon = new Summon();

        summon.setId(this.getId());
        summon.setTitle(this.getTitle());
        summon.setDuration(this.getDuration());
        summon.setHealth(this.getHealth());
        summon.setAction(this.getAction());
        summon.setEffects(this.getRawEffects());

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
    }

    @Override
    public String getValidableValue() {
        return getTitle();
    }
    //endregion
}
