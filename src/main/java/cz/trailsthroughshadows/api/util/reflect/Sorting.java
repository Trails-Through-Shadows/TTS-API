package cz.trailsthroughshadows.api.util.reflect;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

@Slf4j
public class Sorting {
    public Sorting() {
        throw new RuntimeException("This class is not instantiable");
    }

    public static <T> int compareTo(T first, T second, List<String> sort) {
        if (sort.isEmpty()) {
            return 0;
        }

        return sort.stream()
                .mapToInt(sortItem -> Sorting.compareTo(first, second, sortItem))
                .sum();
    }

    public static <T, U extends Comparable<U>> int compareTo(T first, T second, String sort) {
        if (sort.isEmpty()) {
            return 0;
        }

        String[] sortSplit = sort.split(":");
        if (sortSplit.length != 2) {
            log.warn("Invalid sort item: {}", sort);
            return 0;
        }

        String sortKey = sortSplit[0];
        String sortDirection = sortSplit[1];

        try {
            Field firstField = Ref.getField(first.getClass(), sortKey);
            firstField.setAccessible(true);
            Object firstValue = firstField.get(first);

            Field secondField = Ref.getField(second.getClass(), sortKey);
            secondField.setAccessible(true);
            Object secondValue = secondField.get(second);

            // Sorting types must match
            if (!firstValue.getClass().equals(secondValue.getClass())) {
                throw new IllegalArgumentException("Field values are not of same type");
            }

            // When sorting list, sort by list size
            if (firstValue instanceof Enumeration<?> || firstValue instanceof Collection<?>) {
                firstValue = ((Collection<?>) firstValue).size();
                secondValue = ((Collection<?>) secondValue).size();
            }

            if (!(firstValue instanceof Comparable<?>) || !(secondValue instanceof Comparable<?>)) {
                throw new IllegalArgumentException("Field values are not comparable");
            }

            return switch (sortDirection) {
                case "asc" -> ((U) firstValue).compareTo((U) secondValue);
                case "desc" -> ((U) secondValue).compareTo((U) firstValue);
                default -> throw new IllegalArgumentException("Unknown sort direction: " + sortDirection);
            };
        } catch (Exception e) {
            log.error("Error in sorting: ", e);
            return 0;
        }
    }
}
