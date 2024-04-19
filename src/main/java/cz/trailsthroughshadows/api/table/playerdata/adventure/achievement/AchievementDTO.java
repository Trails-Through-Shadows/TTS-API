package cz.trailsthroughshadows.api.table.playerdata.adventure.achievement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Description;
import cz.trailsthroughshadows.algorithm.validation.text.Title;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Achievement")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AchievementDTO extends Validable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int xpReward;

    //region Validation
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        validateChild(new Description(description), validationConfig);
        validateChild(new Title(title), validationConfig);

        // xp reward must be positive
        if (xpReward <= 0) {
            errors.add(new ValidationError("Achievement", "xpReward", xpReward, "XP reward must be positive."));
        }
    }

    @Override
    public String getValidableValue() {
        return title;
    }
    //endregion
}
