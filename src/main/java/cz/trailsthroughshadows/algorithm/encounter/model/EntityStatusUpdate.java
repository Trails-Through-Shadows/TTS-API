package cz.trailsthroughshadows.algorithm.encounter.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EntityStatusUpdate {

    public int id;
    public int health;
    public List<EncounterEffect> effects;
    public Status status;

    public enum Status {
        ALIVE,
        DEAD
    }
}
