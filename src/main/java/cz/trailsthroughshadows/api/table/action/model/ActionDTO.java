package cz.trailsthroughshadows.api.table.action.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Description;
import cz.trailsthroughshadows.algorithm.validation.text.Title;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.action.features.attack.Attack;
import cz.trailsthroughshadows.api.table.action.features.movement.Movement;
import cz.trailsthroughshadows.api.table.action.features.restorecards.RestoreCards;
import cz.trailsthroughshadows.api.table.action.features.skill.Skill;
import cz.trailsthroughshadows.api.table.action.features.summon.SummonAction;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Action")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ActionDTO extends Validable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(nullable = false, length = 128)
    private String title;

    @Column
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private Discard discard;

    @Column
    private Integer levelReq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movement")
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private Movement movement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill")
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private Skill skill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attack")
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private Attack attack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restoreCards")
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private RestoreCards restoreCards;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "idAction")
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private List<SummonAction> summonActions = List.of();

    //region Validation
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Title and description have to be valid.
        Title title = new Title(getTitle());
        validateChild(title, validationConfig);

        Description description = new Description(getDescription());
        validateChild(description, validationConfig);

        // Level requirement must be greater than 0, if it is not null.
        if (levelReq != null && levelReq < 0) {
            errors.add(new ValidationError(getValidableClass(), "levelReq", levelReq, "Level requirement must be greater than 0!"));
        }

        // All features must be validated.
        for (SummonAction summonAction : summonActions) {
            validateChild(summonAction.getSummon(), validationConfig);
        }
        validateChild(movement, validationConfig);
        validateChild(skill, validationConfig);
        validateChild(attack, validationConfig);
        validateChild(restoreCards, validationConfig);
    }

    @Override
    public String getValidableValue() {
        return getTitle();
    }
    //endregion

    public enum Discard implements Serializable {
        PERMANENT,
        SHORT_REST,
        LONG_REST,
        NEVER
    }
}
