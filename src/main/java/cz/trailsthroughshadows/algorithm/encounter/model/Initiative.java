package cz.trailsthroughshadows.algorithm.encounter.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Initiative {

    private Integer id;
    private Integer initiative;

//    @JsonIgnore
//    private List<Integer> summons = new ArrayList<>();

    private EncounterEntity.EntityType type;

//    public Initiative(Integer id, Integer initiative, List<Integer> summons, EncounterEntity.EntityType type) {
//        this.id = id;
//        this.initiative = initiative;
//        this.summons = summons;
//        this.type = type;
//    }

    public Initiative(Integer id, Integer initiative, EncounterEntity.EntityType type) {
        this.id = id;
        this.initiative = initiative;
        this.type = type;
    }
}
