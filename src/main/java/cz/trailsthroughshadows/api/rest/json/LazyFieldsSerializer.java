package cz.trailsthroughshadows.api.rest.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Id;
import jakarta.persistence.Persistence;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.proxy.HibernateProxy;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Serializer which serialize non fetched lazy fields as ID or array of IDs.
 */
@Slf4j
public class LazyFieldsSerializer extends com.fasterxml.jackson.databind.JsonSerializer<Object> {
    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else if (!Persistence.getPersistenceUtil().isLoaded(value)) {
            if (value instanceof Collection) {
                gen.writeStartArray();

                for (Object o : (Collection) value) {

                    // serialize only @Id or @EmbeddedId
                    log.trace("serializing Object: {}, has {} fields", o.getClass().getSimpleName(), o.getClass().getDeclaredFields().length);

                    for (Field f : o.getClass().getDeclaredFields()) {
                        f.setAccessible(true);
                        if (f.isAnnotationPresent(Id.class) || f.isAnnotationPresent(EmbeddedId.class)) {
                            log.trace("     serializing Field: {}", f.getName());
                            try {
                                Object fieldValue = f.get(o);
                                gen.writeObject(fieldValue);
                                //gen.writeObjectField(f.getName(), fieldValue);
                            } catch (IllegalAccessException e) {
                                log.error("Error accessing field value", e);
                            }
                        }
                    }
                    //gen.writeObject(o);
                }
                gen.writeEndArray();
            } else {
                gen.writeObject(((HibernateProxy) value).getHibernateLazyInitializer().getIdentifier());
            }
            //TODO?? MAYBE?? ignore fields which are annotated with @Id or @Embedded id in m:n tables
        } else {
            gen.writeObject(value);
        }
    }
}
