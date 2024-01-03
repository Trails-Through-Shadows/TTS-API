package cz.trailsthroughshadows.api.util.reflect;

import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class Ref {
    private static final Map<Class<?>, Map<String, Field>> fieldCache = new HashMap<>();

    public Ref() {
        throw new RuntimeException("This class is not instantiable");
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        Map<String, Field> fields = fieldCache.computeIfAbsent(clazz, k -> new HashMap<>());

        return fields.computeIfAbsent(fieldName, fn -> {
            try {
                Field field = clazz.getDeclaredField(fn);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Object parseValue(Field field, String value) {
        try {
            return switch (field.getType().getSimpleName()) {
                case "String" -> value;
                case "int", "Integer" -> Integer.parseInt(value);
                case "boolean", "Boolean" -> Boolean.parseBoolean(value);
                case "double", "Double" -> Double.parseDouble(value);
                case "float", "Float" -> Float.parseFloat(value);
                case "long", "Long" -> Long.parseLong(value);
                default -> throw new NumberFormatException("Unknown type: " + field.getType().getSimpleName());
            };
        } catch (NumberFormatException e) {
            log.warn("Invalid filter value: {}", value);
            throw e;
        }
    }

    public static boolean compare(Object fieldValue, Object value, String operator) {
        if (!(fieldValue instanceof Comparable comparableField) || !(value instanceof Comparable comparableValue)) {
            throw new IllegalArgumentException("Field and value must be comparable");
        }

        return switch (operator) {
            case "gt" -> comparableField.compareTo(comparableValue) > 0;
            case "gte" -> comparableField.compareTo(comparableValue) >= 0;
            case "lt" -> comparableField.compareTo(comparableValue) < 0;
            case "lte" -> comparableField.compareTo(comparableValue) <= 0;
            default -> false;
        };
    }
}
