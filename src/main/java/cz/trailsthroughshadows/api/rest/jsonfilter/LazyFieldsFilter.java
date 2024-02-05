package cz.trailsthroughshadows.api.rest.jsonfilter;

import jakarta.persistence.Persistence;

public class LazyFieldsFilter {
    @Override
    public boolean equals(Object obj) {

        if (obj == null)
            return true;

        return !Persistence.getPersistenceUtil().isLoaded(obj);

    }
}
