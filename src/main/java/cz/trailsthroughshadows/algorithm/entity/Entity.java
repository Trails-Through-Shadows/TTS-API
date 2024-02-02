package cz.trailsthroughshadows.algorithm.entity;

import cz.trailsthroughshadows.api.table.effect.Effect;
import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import jakarta.persistence.Transient;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class Entity {

    @Transient
    public Hex hex;
    @Transient
    public List<Effect> activeEffects = new ArrayList<>();

    public abstract String getName();

//    public abstract List<Action> getActions();
}
