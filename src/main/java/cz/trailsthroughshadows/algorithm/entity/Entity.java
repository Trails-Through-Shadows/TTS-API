package cz.trailsthroughshadows.algorithm.entity;

import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
public class Entity {

    @Transient
    public Hex hex;
}
