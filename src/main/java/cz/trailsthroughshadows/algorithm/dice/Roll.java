package cz.trailsthroughshadows.algorithm.dice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class Roll {
    private final Type type;

    private final int value;

    public Roll(Type type) {
        this.type = type;
        this.value = 0;
    }

    public int getValue() {
        return switch (type) {
            case NUMBER -> value;
            case CRIT -> 10;
            case MISS -> -10;
        };
    }

    @Getter
    public enum Type {
        CRIT,
        MISS,
        NUMBER
    }
}
