package cz.trailsthroughshadows.algorithm.util;

import java.util.Random;

public class Color {

    public static String getRandom() {
        Random random = new Random();

        int minBrightness = 64;
        int maxBrightness = 192;

        int red = random.nextInt(maxBrightness - minBrightness) + minBrightness;
        int green = random.nextInt(maxBrightness - minBrightness) + minBrightness;
        int blue = random.nextInt(maxBrightness - minBrightness) + minBrightness;

        return String.format("#%02X%02X%02X", red, green, blue);
    }
}
