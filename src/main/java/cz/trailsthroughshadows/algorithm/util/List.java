package cz.trailsthroughshadows.algorithm.util;

import java.util.ArrayList;

public class List {
    public static <T> T getRandom(T[] array) {
        return array[(int) (Math.random() * array.length)];
    }

    @SafeVarargs
    public static <T> java.util.List<T> union(java.util.List<? extends T>... lists) {
        ArrayList<T> union = new ArrayList<>();
        for (java.util.List<? extends T> list : lists) {
            union.addAll(list);
        }
        return union;
    }
}
