package cz.trailsthroughshadows.api.table.campaign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Description;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
    private StoryTrigger trigger;

    @Column(nullable = false)
    private String story;

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


    public enum StoryTrigger implements Serializable {
        START, END
    }
}
