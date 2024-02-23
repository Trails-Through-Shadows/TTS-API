package cz.trailsthroughshadows.algorithm.util;

import lombok.experimental.UtilityClass;

import java.awt.*;

@UtilityClass
public class TextUtils {

    public static Integer getTextWidth(String text) {
        return getTextWidth(text, new Font("Arial", Font.PLAIN, 12));
    }

    public static Integer getTextWidth(String text, Font font) {
        return new Canvas().getFontMetrics(font).stringWidth(text);
    }
}
