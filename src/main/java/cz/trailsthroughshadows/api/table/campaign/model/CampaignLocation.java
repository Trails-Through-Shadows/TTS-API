package cz.trailsthroughshadows.api.table.campaign.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.encounter.Encounter;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "CampaignLocation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CampaignLocation {

    @Id
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idLocation", insertable = false, updatable = false)
    private LocationDTO location;

    @Column(nullable = false, insertable = false, updatable = false)
    private Integer idCampaign;

    @OneToMany(mappedBy = "id", fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private List<Story> stories;

    @Column(nullable = false)
    private Boolean start;

    @Column(nullable = false)
    private Boolean finish;

    @Column(nullable = false, name = "condition")
    private String conditionString;

    @Transient
    private List<Condition> conditions;

    @PostLoad
    private void postLoad() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = conditionString.replaceAll("\\\\", "");
            conditions = mapper.readValue(tmp, mapper.getTypeFactory().constructCollectionType(List.class, Condition.class));
        } catch (Exception e) {
            conditions = List.of();
        }
    }

    @JsonIgnore
    public void setConditionString(String conditionString) {
        this.conditionString = conditionString;
        postLoad();
    }

    @JsonIgnore
    public String getConditionString() {
        return conditionString;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Condition extends Validable implements Serializable {
        @Override
        protected void validateInner(@Nullable ValidationConfig validationConfig) {
            if (type == null) {
                errors.add(new ValidationError("Condition", "type", null, "Type must not be null."));
            }
            if (value == null) {
                errors.add(new ValidationError("Condition", "value", null, "Value must not be null."));
            }
            if (result == null) {
                errors.add(new ValidationError("Condition", "result", null, "Result must not be null."));
            }

            // result has to be either COMPLETED or FAILED
            if (result != Encounter.EncounterState.COMPLETED && result != Encounter.EncounterState.FAILED) {
                errors.add(new ValidationError("Condition", "result", result, "Result must be either COMPLETED or FAILED."));
            }
        }

        @Override
        public String getValidableValue() {
            return null;
        }

        public enum Type {
            ENEMY_DEATHS,
            PLAYER_DEATHS,
            DOORS_OPENED,
            ROUND_REACHED
        }

        private Type type;
        private Integer value;
        private Encounter.EncounterState result;

        @JsonIgnore
        private Integer progression;
    }

}

