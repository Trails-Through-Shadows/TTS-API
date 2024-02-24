package cz.trailsthroughshadows.api.table.action.features.skill;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.foraction.SkillEffect;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "Skill")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Skill extends Validable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(nullable = false)
    private Integer range;

    @Column(nullable = false)
    private Integer area;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EffectDTO.EffectTarget target;

    @OneToMany(mappedBy = "key.idSkill", fetch = FetchType.LAZY)
    //@ToString.Exclude
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private Collection<SkillEffect> effects;


    // Skipping n-to-n relationship, there is no additional data in that table
    //@ToString.Include(name = "effects") // Including replacement field in toString
    @JsonIgnore
    public Collection<EffectDTO> getMappedEffects() {
        if (effects == null) return new ArrayList<>();
        return effects.stream().map(SkillEffect::getEffect).toList();
    }

    //region Validation
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Range and target cant be null.
        if (range == null) {
            errors.add(new ValidationError("Skill", "range", null, "Range must not be null."));
        }
        if (target == null) {
            errors.add(new ValidationError("Skill", "target", null, "Target must not be null."));
        }

        if (!errors.isEmpty()) return;

        // Range must be greater than 0. It can be 0 only if target is SELF.
        if (range < 0 || (range == 0 && target != EffectDTO.EffectTarget.SELF)) {
            errors.add(new ValidationError("Skill", "range", getRange(), "Range must be greater than 0."));
        }

        // Area of effect must be greater than or equal to 0.
        if (area != null && area < 0) {
            errors.add(new ValidationError("Skill", "area", getArea(), "Area of effect must be greater than 0."));
        }

        // All effects must be validated.
        for (var e : getMappedEffects()) {
            if (e == null) {
                errors.add(new ValidationError("Skill", "effects", null, "Skill must not contain null effects."));
                break;
            }
            validateChild(e, validationConfig);
        }
    }

    @Override
    public String getValidableValue() {
        StringBuilder res = new StringBuilder();
        Collection<EffectDTO> effects = getMappedEffects().stream().filter(e -> e != null).filter(e -> e.getType() != null).toList();
        List<String> effectNames = effects.stream()
                .map(EffectDTO::getType)
                .map(EffectDTO.EffectType::name)
                .toList();
        res.append(String.join(", ", effectNames));
        return (res.isEmpty() ? "Nothing" : res) + " within " + getRange() + " hexes.";
    }
    //endregion
}
