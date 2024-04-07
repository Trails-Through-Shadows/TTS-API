package cz.trailsthroughshadows.api.util.reflect;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import java.lang.reflect.Field;
import java.util.*;

@Log4j2
public class Initialization {

    /**
     * Initializes all fields of the given entity according to filter in recursive
     * way.
     * Can take Collection or single object
     *
     * @param entity Entity to initialize, can be Collection or simple object
     * @param filter White list of fields to initialize
     */
    public static void hibernateInitializeAll(Object entity, List<String> filter) {
        if (filter.contains("NONE"))
            throw new IllegalArgumentException("NONE is reserved word for filter");
        if (entity instanceof Collection<?>) {
            throw new IllegalArgumentException("Entity can't be Collection");
        }
        Map<Object, Integer> visited = new HashMap<>();
        hibernateInitializeAll(entity, visited, filter);
    }

    /**
     * Initializes all fields of the given entity in recursive way.
     * Only simple object
     *
     * @param entity Entity to initialize, can't be Collection
     */
    public static void hibernateInitializeAll(Object entity) {
        if (entity instanceof Collection<?>) {
            throw new IllegalArgumentException("Entity can't be Collection");
        }
        List<String> filter = new ArrayList<>();
        filter.add("NONE");
        Map<Object, Integer> visited = new HashMap<>();
        hibernateInitializeAll(entity, visited, filter);
    }

    private static void hibernateInitializeAll(Object entity, Map<Object, Integer> visited, List<String> filter) {
        if (visited.containsKey(entity)) {
            if ((visited.get(entity) > 10)) {
                log.error("There is more than 10 entites in this class {}", entity.getClass().getSimpleName());
                return;
            }
        }

        visited.put(entity.getClass(), visited.getOrDefault(entity.getClass(), 0) + 1);

        log.trace("Initializing object: {}", entity.getClass().getSimpleName());
        Hibernate.initialize(entity);

        if (entity instanceof HibernateProxy) {
            entity = ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }

        // getting fields
        log.trace("getting fields for class \nsimple name:{}\n get name:{} ", entity.getClass().getSimpleName(),
                entity.getClass().getName());
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (!(filter.contains(field.getName()) || filter.contains("NONE"))) {
                continue;
            }
            if (!(field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToOne.class))) {
                continue;
            }

            log.trace("     Initializing property: {}", field.getName());
            Hibernate.initialize(field);
            try {
                Object child = field.get(entity);
                if (child != null && !isPrimitiveOrWrapper(child.getClass())) {
                    log.trace("         Initializing child Class: {}", child.getClass());

                    if (child instanceof Collection) {
                        for (Object item : (Collection) child) {
                            hibernateInitializeAll(item, visited, filter);
                        }
                    } else {
                        hibernateInitializeAll(child, visited, filter);
                    }

                    field.set(entity, child);
                    log.trace("                  Initialized child Class: {}", child.getClass());

                }
            } catch (IllegalAccessException e) {
                log.error("Error accessing field value", e);
            }
        }

    }

    private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || clazz.equals(String.class) || clazz.equals(Integer.class)
                || clazz.equals(Long.class) || clazz.equals(Double.class) || clazz.equals(Float.class)
                || clazz.equals(Character.class) || clazz.equals(Byte.class) || clazz.equals(Short.class)
                || clazz.equals(Boolean.class) || clazz.isEnum();

    }

}
