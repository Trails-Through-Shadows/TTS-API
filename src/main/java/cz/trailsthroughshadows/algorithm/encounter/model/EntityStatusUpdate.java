package cz.trailsthroughshadows.algorithm.encounter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EntityStatusUpdate {

    public EncounterEntity.EntityType type;
    public int id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer idGroup;
    public int health;
    public List<EncounterEffect> effects;
    public Status status;

    public enum Status {
        ALIVE,
        DEAD
    }
}
