package cz.trailsthroughshadows.api.rest.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Serializer which serialize non fetched lazy fields as ID or array of IDs.
 */
@Slf4j
public class LazyFieldsSerializer extends com.fasterxml.jackson.databind.JsonSerializer<Object> {

    private void writeFieldsWithoutLazy(Object value, JsonGenerator gen) throws IOException {
        gen.writeStartObject();
        for (Field f : value.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (f.isAnnotationPresent(OneToMany.class) || f.isAnnotationPresent(ManyToOne.class) || f.isAnnotationPresent(ManyToMany.class)) {
                continue;
            }
            try {
                Object fieldValue = f.get(value);
                gen.writeObjectField(f.getName(), fieldValue);
            } catch (IllegalAccessException e) {
                log.error("Error accessing field value", e);
            } catch (IOException e) {
                log.error("Error writing field value", e);
            } catch (Exception e) {
                log.error("Error", e);
            }
        }
        gen.writeEndObject();
    }

    private void writeCollectionFieldsWithoutLazy(Object value, JsonGenerator gen) throws IOException {
        gen.writeStartArray();
        for (Object o : (Collection) value) {
            writeFieldsWithoutLazy(o, gen);
        }
        gen.writeEndArray();
    }


    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else if (!Hibernate.isInitialized(value)) {
            if (value instanceof Collection) {
                writeCollectionFieldsWithoutLazy(value, gen);
            } else {
                gen.writeObject(((HibernateProxy) value).getHibernateLazyInitializer().getIdentifier());
            }
            // TO.DO?? MAYBE?? ignore fields which are annotated with @Id or @Embedded id in m:n tables
            // nope bcs it woudnt deserialize properly
        } else {
            gen.writeObject(value);
        }
    }
}
