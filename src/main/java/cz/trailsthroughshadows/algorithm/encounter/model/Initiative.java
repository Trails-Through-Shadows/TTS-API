package cz.trailsthroughshadows.algorithm.encounter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Initiative {

    private Integer id;
    private Integer initiative;

    private EncounterEntity.EntityType type;
}
