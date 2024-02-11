package cz.trailsthroughshadows.api.rest.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import jakarta.persistence.Persistence;
import org.hibernate.proxy.HibernateProxy;

import java.io.IOException;
import java.util.Collection;

/**
 * Serializer which serialize non fetched lazy fields as ID or array of IDs.
 */
public class LazyFieldsSerializer extends com.fasterxml.jackson.databind.JsonSerializer<Object> {
    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else if (!Persistence.getPersistenceUtil().isLoaded(value)) {
            if (value instanceof Collection) {
                gen.writeStartArray();
                for (Object o : (Collection) value) {
                    gen.writeObject(o);
                }
                gen.writeEndArray();
            } else {
                gen.writeObject(((HibernateProxy) value).getHibernateLazyInitializer().getIdentifier());
            }
        } else {
            gen.writeObject(value);
        }
    }
}
