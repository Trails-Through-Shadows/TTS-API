package cz.trailsthroughshadows.algorithm.dice;

import java.util.List;

public class Dice {
    public final List<Roll> rolls;

    public Dice(List<Roll> pairs) {
        this.rolls = pairs;
    }

    // TODO move to database
    public static Dice dCharacter = new Dice(List.of(
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

            new Roll(Roll.Type.MISS),
            new Roll(Roll.Type.CRIT),
            new Roll(Roll.Type.SPECIAL, 1),
            new Roll(Roll.Type.SPECIAL, 2)
    ));

    public Roll roll() {
        return rolls.get((int) (Math.random() * rolls.size()));
    }
}
