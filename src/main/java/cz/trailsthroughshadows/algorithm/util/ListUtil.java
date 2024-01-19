package cz.trailsthroughshadows.algorithm.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
    public static <T> T getRandom(T[] array) {
        return array[(int) (Math.random() * array.length)];
    }

    @SafeVarargs
    public static <T> List<T> union(List<? extends T> ... lists) {
        ArrayList<T> union = new ArrayList<>();
        for (List<? extends T> list : lists) {
            union.addAll(list);
        }
        return union;
    }
}
