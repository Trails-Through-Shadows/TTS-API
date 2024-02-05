package cz.trailsthroughshadows.api.util.reflect;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class Initialization {

    /**
     * Initializes all fields of the given entity in recursive way.
     * @param entity Entity to initialize
     */
    public static void hibernateInitializeAll(Object entity) {
        Map<Object, Integer> visited = new HashMap<>();
        hibernateInitializeAll(entity, visited);
    }

    private static void hibernateInitializeAll(Object entity, Map<Object, Integer> visited) {

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


        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            //log.debug("     Initializing property: {}", field.getName());
            try {
                Object child = field.get(entity);
                if (child != null && !isPrimitiveOrWrapper(child.getClass())) {
                    Hibernate.initialize(child);
                    if (child instanceof Collection) {
                        for (Object item : (Collection) child) {
                            hibernateInitializeAll(item, visited);
                        }
                    } else {
                        hibernateInitializeAll(child, visited);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(Long.class) || clazz.equals(Double.class) || clazz.equals(Float.class) || clazz.equals(Character.class) || clazz.equals(Byte.class) || clazz.equals(Short.class) || clazz.equals(Boolean.class);
    }

}
