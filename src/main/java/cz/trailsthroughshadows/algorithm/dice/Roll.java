package cz.trailsthroughshadows.algorithm.dice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class Roll{
    @Getter
    public enum Type {
        CRIT,
        MISS,
        SPECIAL,
        NUMBER;
    }

    private final Type type;
    private final int value;

    public Roll(Type type) {
        this.type = type;
        this.value = 0;
    }
}
