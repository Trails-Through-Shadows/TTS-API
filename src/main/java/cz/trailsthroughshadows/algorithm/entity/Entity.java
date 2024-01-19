package cz.trailsthroughshadows.algorithm.entity;

import cz.trailsthroughshadows.api.table.action.Action;
import cz.trailsthroughshadows.api.table.effect.Effect;
import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class Entity {

    enum CombatStyle {
        MELEE,
        RANGED,
    }

    @Transient
    public Hex hex;

    @Transient
    public List<Effect> activeEffects = new ArrayList<>();

    @Column(nullable = false)
    public CombatStyle combatStyle = CombatStyle.MELEE;

    public abstract String getName();

    public abstract List<Action> getActions();
}
