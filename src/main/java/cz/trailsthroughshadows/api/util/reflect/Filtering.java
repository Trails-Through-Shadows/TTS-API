package cz.trailsthroughshadows.api.util.reflect;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;

@Slf4j
public class Filtering {
    public Filtering() {
        throw new RuntimeException("This class is not instantiable");
    }

    public static <T> boolean match(T object, List<String> filter) {
        return filter.isEmpty() || filter.stream().allMatch(filterItem -> Filtering.match(object, filterItem));
    }

    public static <T> boolean match(T object, String filter) {
        if (filter.isEmpty()) {
            return true;
        }

        String[] filterSplit = filter.split(":");
        if (filterSplit.length != 3) {
            log.warn("Invalid filter: {}", filter);
            return true;
        }

        String filterKey = filterSplit[0];
        String filterOperator = filterSplit[1];
        String filterValue = filterSplit[2];

        try {
            Field field = Ref.getField(object.getClass(), filterKey);
            Object fieldValue = field.get(object);
            Object parsedValue = Ref.parseValue(field, filterValue);

            return switch (filterOperator) {
                case "eq" -> fieldValue.equals(parsedValue);
                case "gt", "gte", "lt", "lte" -> Ref.compare(fieldValue, parsedValue, filterOperator);
                case "has" -> fieldValue.toString().contains(filterValue);
                default -> false;
            };
        } catch (Exception e) {
            log.error("Error in filtering: ", e);
            return false;
        }
    }
}
