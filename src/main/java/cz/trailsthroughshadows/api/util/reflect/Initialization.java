package cz.trailsthroughshadows.api.util.reflect;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import java.lang.reflect.Field;
import java.util.*;

@Log4j2
public class Initialization {

    /**
     * Initializes all fields of the given entity according to filter in recursive way.
     *
     * @param entity Entity to initialize
     * @param filter White list of fields to initialize
     */
    public static void hibernateInitializeAll(Object entity, List<String> filter) {
        Map<Object, Integer> visited = new HashMap<>();
        hibernateInitializeAll(entity, visited, filter);
    }

    /**
     * Initializes all fields of the given entity in recursive way.
     *
     * @param entity Entity to initialize
     */
    public static void hibernateInitializeAll(Object entity) {
        List<String> filter = new ArrayList<>();
        Map<Object, Integer> visited = new HashMap<>();
        hibernateInitializeAll(entity, visited, filter);
    }

    private static void hibernateInitializeAll(Object entity, Map<Object, Integer> visited, List<String> filter) {

        if (entity == null || visited.containsKey(entity)) {
            if ((visited.get(entity) > 10))
                return;
        }

        visited.put(entity, visited.getOrDefault(entity, 0) + 1);

        //log.debug("Initializing object: {}", entity.getClass().getSimpleName());
        Hibernate.initialize(entity);

        if (entity instanceof HibernateProxy) {
            entity = ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }

        // getting fields
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (!(filter.contains(field.getName()) || filter.isEmpty())) {
                continue;
            }

            log.trace("     Initializing property: {}", field.getName());

            try {
                Object child = field.get(entity);
                if (child != null && !isPrimitiveOrWrapper(child.getClass())) {
                    Hibernate.initialize(child);
                    log.trace("         Initializing item: {}", child.getClass().getSimpleName());
                    if (child instanceof Collection) {
                        for (Object item : (Collection) child) {
                            hibernateInitializeAll(item, visited, filter);
                        }
                    } else {
                        hibernateInitializeAll(child, visited, filter);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(Long.class) || clazz.equals(Double.class) || clazz.equals(Float.class) || clazz.equals(Character.class) || clazz.equals(Byte.class) || clazz.equals(Short.class) || clazz.equals(Boolean.class) || clazz.isEnum();

    }

}
