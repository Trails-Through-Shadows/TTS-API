package cz.trailsthroughshadows.algorithm.encounter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Initiative {

    private EncounterEntity.EntityType type;
    private Integer id;
    private Integer initiative;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ActionDTO action;

//    @JsonIgnore
//    private List<Integer> summons = new ArrayList<>();

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
