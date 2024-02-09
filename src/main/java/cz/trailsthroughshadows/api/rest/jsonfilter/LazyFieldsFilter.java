package cz.trailsthroughshadows.api.rest.jsonfilter;

import jakarta.persistence.Persistence;
import org.hibernate.proxy.HibernateProxy;

import java.util.Collection;

public class LazyFieldsFilter {
    @Override
    public boolean equals(Object obj) {

        if (obj == null)
            return false;

        if (obj instanceof Collection)
            return false;

//        if (obj instanceof HibernateProxy)
//            return false;

        if (Persistence.getPersistenceUtil().isLoaded(obj))
            return false;

        return true;
    }
}
