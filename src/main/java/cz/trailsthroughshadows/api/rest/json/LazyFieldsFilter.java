package cz.trailsthroughshadows.api.rest.json;

import jakarta.persistence.Persistence;

import java.util.Collection;

/**
 * Filter non fetched lazy fields from serialization.
 */
public class LazyFieldsFilter {
    @Override
    public boolean equals(Object obj) {

        if (obj == null)
            return false;

        if (obj instanceof Collection)
            return false;

//        if (obj instanceof HibernateProxy)
//            return false;

        return !Persistence.getPersistenceUtil().isLoaded(obj);
    }
}
