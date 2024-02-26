package cz.trailsthroughshadows.algorithm.dice;

import java.util.List;

public class Dice {
    // TODO move to database
    public static Dice character = new Dice(List.of(
            new Roll(Roll.Type.NUMBER, -3),
            new Roll(Roll.Type.NUMBER, -3),
            new Roll(Roll.Type.NUMBER, -2),
            new Roll(Roll.Type.NUMBER, -2),
            new Roll(Roll.Type.NUMBER, -1),
            new Roll(Roll.Type.NUMBER, -1),
            new Roll(Roll.Type.NUMBER, 0),
            new Roll(Roll.Type.NUMBER, 0),
            new Roll(Roll.Type.NUMBER, 1),
            new Roll(Roll.Type.NUMBER, 1),
            new Roll(Roll.Type.NUMBER, 2),
            new Roll(Roll.Type.NUMBER, 2),
            new Roll(Roll.Type.NUMBER, 3),
            new Roll(Roll.Type.NUMBER, 3),
            new Roll(Roll.Type.NUMBER, 4),
            new Roll(Roll.Type.NUMBER, 4),
            new Roll(Roll.Type.NUMBER, 5),
            new Roll(Roll.Type.NUMBER, 5),

            new Roll(Roll.Type.MISS),
            new Roll(Roll.Type.CRIT)
    ));

    public static Dice enemy = new Dice(List.of(
            new Roll(Roll.Type.NUMBER, -3),
            new Roll(Roll.Type.NUMBER, -3),
            new Roll(Roll.Type.NUMBER, -2),
            new Roll(Roll.Type.NUMBER, -2),
            new Roll(Roll.Type.NUMBER, -1),
            new Roll(Roll.Type.NUMBER, -1),
            new Roll(Roll.Type.NUMBER, 0),
            new Roll(Roll.Type.NUMBER, 0),
            new Roll(Roll.Type.NUMBER, 0),
            new Roll(Roll.Type.NUMBER, 0),
            new Roll(Roll.Type.NUMBER, 1),
            new Roll(Roll.Type.NUMBER, 1),
            new Roll(Roll.Type.NUMBER, 2),
            new Roll(Roll.Type.NUMBER, 2),
            new Roll(Roll.Type.NUMBER, 3),
            new Roll(Roll.Type.NUMBER, 3),
            new Roll(Roll.Type.NUMBER, 4),
            new Roll(Roll.Type.NUMBER, 4),
            new Roll(Roll.Type.NUMBER, 5),
            new Roll(Roll.Type.NUMBER, 5)
    ));

    public final List<Roll> rolls;

    public Dice(List<Roll> pairs) {
        this.rolls = pairs;
    }

    public Roll roll() {
        return rolls.get((int) (Math.random() * rolls.size()));
    }
}
