package cz.trailsthroughshadows.api.table.campaign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.trailsthroughshadows.algorithm.encounter.Encounter;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Description;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "Story")
public class Story extends Validable {
    @Id
    private Integer id;

    @Column(nullable = false)
    private Integer idCampaignLocation;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Encounter.EncounterState trigger;

    @Column(nullable = false)
    private String story;

    public static Story about(Encounter.EncounterState trigger) {
        String text = switch (trigger) {
            case NEW -> "Your story begins.";
            case ONGOING -> "Your story is still being written.";
            case COMPLETED -> "Your story had a good ending.";
            case FAILED -> "Your story had a bad ending.";
            default -> throw new IllegalStateException("Unexpected value: " + trigger);
        };

        return new Story(0, 0, trigger, text);
    }

    //region Validation
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        if (trigger == null) {
            errors.add(new ValidationError("Story", "trigger", null, "Trigger must not be null."));
        }

        validateChild(new Description(story), validationConfig, "story");
    }

    @Override
    public String getValidableValue() {
        return trigger.toString();
    }
    //endregion

}
