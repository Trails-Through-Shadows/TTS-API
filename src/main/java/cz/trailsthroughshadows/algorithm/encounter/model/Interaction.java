package cz.trailsthroughshadows.algorithm.encounter.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Interaction {

    private int damage;
    private final List<EncounterEffect> effects = new ArrayList<>();
}
